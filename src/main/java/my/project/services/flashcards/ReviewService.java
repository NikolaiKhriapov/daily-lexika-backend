package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.flashcards.ReviewStatisticsDTO;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import my.project.models.mapper.flashcards.ReviewMapper;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.mapper.flashcards.WordMapper;
import my.project.models.entity.flashcards.*;
import my.project.repositories.flashcards.ReviewRepository;
import my.project.repositories.user.UserRepository;
import my.project.services.user.AuthenticationService;
import my.project.services.user.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static my.project.models.entity.enumeration.Status.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final WordMapper wordMapper;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordPackService wordPackService;
    private final AuthenticationService authenticationService;
    private final RoleService roleService;
    private final MessageSource messageSource;

    @Transactional
    public List<ReviewDTO> getAllReviews() {
        User user = authenticationService.getAuthenticatedUser();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Review> allReviews = reviewRepository.findAllByUserIdAndPlatform(user.getId(), platform);

        List<ReviewDTO> allReviewDTOs = new ArrayList<>();
        for (Review oneReview : allReviews) {
            if (!Objects.equals(oneReview.getDateGenerated(), LocalDate.now())) {
                reviewRepository.delete(oneReview);
                reviewRepository.save(generateReview(reviewMapper.toDTO(oneReview), user.getId()));
            }
            allReviewDTOs.add(reviewMapper.toDTO(oneReview));
        }

        return allReviewDTOs;
    }

    public ReviewDTO getReviewById(Long reviewId) {
        return reviewMapper.toDTO(getReview(reviewId));
    }

    public ReviewDTO createReview(ReviewDTO newReviewDTO) {
        User user = authenticationService.getAuthenticatedUser();

        deleteReviewIfAlreadyExists(user, newReviewDTO);

        Review newReview = generateReview(newReviewDTO, user.getId());
        return reviewMapper.toDTO(reviewRepository.save(newReview));
    }

    @Transactional
    public void refreshReview(Long reviewId) {
        Long userId = authenticationService.getAuthenticatedUser().getId();
        Review review = getReview(reviewId);

        List<Word> listOfWords = generateListOfWordsForReview(userId, review.getWordPack(), reviewMapper.toDTO(review));

        review.setDateLastCompleted(null); // TODO::: fix this workaround
        review.setListOfWords(listOfWords);
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    @Transactional
    public Map<String, Object> processReviewAction(Long reviewId, Boolean isCorrect) {
        Review review = getReview(reviewId);
        if (isCorrect != null) {
            List<Word> listOfWords = new ArrayList<>(review.getListOfWords());

            Word thisWord = listOfWords.get(0);
            thisWord.setOccurrence(thisWord.getOccurrence() + 1);

            if (isCorrect) {
                updateWordForCorrectAnswer(thisWord, listOfWords);
            }
            if (!isCorrect) {
                updateWordForIncorrectAnswer(thisWord, listOfWords);
            }

            review.setListOfWords(listOfWords);
            review = reviewRepository.save(review);
        }

        WordDTO reviewWordDTO = showOneReviewWord(review);

        Map<String, Object> map = null;
        if (reviewWordDTO == null) {
            updateUserStreak();
        } else {
            map = new HashMap<>();
            map.put("reviewWordDTO", reviewWordDTO);
            map.put("reviewUpdatedSize", review.getListOfWords().size());
        }

        return map;
    }

    public ReviewStatisticsDTO getReviewStatistics(Long reviewId) {
        Long userId = authenticationService.getAuthenticatedUser().getId();

        Review review = getReview(reviewId);
        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPackName(review.getWordPack().getName());

        Integer newWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, NEW);
        Integer reviewWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, IN_REVIEW);
        Integer knownWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, KNOWN);

        return new ReviewStatisticsDTO(
                review.getId(),
                review.getWordPack().getName(),
                newWords,
                reviewWords,
                knownWords
        );
    }

    /**
     * // Answering in Daily Reviews
     * When NEW –> YES –> KNOWN
     * When NEW -> NO  -> IN_REVIEW
     * When KNOWN –> YES -> KNOWN
     * When KNOWN -> NO  -> IN_REVIEW
     * When IN_REVIEW(0-3) -> YES -> IN_REVIEW(1-4)
     * When IN_REVIEW(4) -> YES -> KNOWN
     * <p>
     * // Generating a Daily Review
     * First, the NEW words are added in the amount of review.maxNewWordsPerDay
     * Then, the IN_REVIEW words are added in the amount of review.maxReviewWordsPerDay * 0.7
     * Then, the KNOWN words are added in the amount of review.maxReviewWordsPerDay * 0.3
     * When it is not enough of the IN_REVIEW words, it is compensated by the KNOWN words, and vice versa
     * <p>
     * IN_REVIEW -> if dateOfLastOccurrence >= totalStreak x2
     * KNOWN -> if dateOfLastOccurrence >= totalStreak
     **/
    public List<Word> generateListOfWordsForReview(Long userId, WordPack wordPack, ReviewDTO reviewDTO) {
        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPackName(wordPack.getName());

        wordService.createOrUpdateWordsForUser(userId, wordDataIds);

        Pageable pageableNew = PageRequest.of(0, reviewDTO.maxNewWordsPerDay());
        List<Word> newWords = wordService.findByUserIdAndWordDataIdInAndStatusIn(
                userId,
                wordDataIds,
                new ArrayList<>(List.of(NEW)),
                pageableNew
        );

        Pageable pageableReviewAndKnown = PageRequest.of(0, reviewDTO.maxReviewWordsPerDay());
        List<Word> reviewAndKnownWords = wordService.findByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrdered(
                userId,
                wordDataIds,
                new ArrayList<>(List.of(IN_REVIEW, KNOWN)),
                pageableReviewAndKnown
        );

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewAndKnownWords);

        listOfWords.forEach(word -> {
            word.setOccurrence(0);
            word.setCurrentStreak(0);
        });

        return listOfWords;
    }

    public void deleteAllByUserIdAndPlatform(Long userId, Platform platform) {
        List<Review> allReviewsByUserId = reviewRepository.findAllByUserIdAndPlatform(userId, platform);
        reviewRepository.deleteAll(allReviewsByUserId);
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.review.notFound", null, Locale.getDefault())));
    }

    private WordDTO showOneReviewWord(Review review) {
        if (!review.getListOfWords().isEmpty()) {
            Word word = review.getListOfWords().get(0);
            return wordMapper.toDTO(word);
        }
        review.setDateLastCompleted(LocalDate.now());
        return null;
    }

    private void deleteReviewIfAlreadyExists(User user, ReviewDTO reviewDTO) {
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Review> existingReviews = reviewRepository.findAllByUserIdAndPlatform(user.getId(), platform);
        for (Review existingReview : existingReviews) {
            if (existingReview.getWordPack().getName().equals(reviewDTO.wordPackDTO().name())) {
                deleteReview(existingReview.getId());
            }
        }
    }

    private Review generateReview(ReviewDTO reviewDTO, Long userId) {
        WordPack wordPack = wordPackService.getWordPackByName(reviewDTO.wordPackDTO().name());

        List<Word> listOfWords = generateListOfWordsForReview(userId, wordPack, reviewDTO);

        return new Review(
                userId,
                reviewDTO.maxNewWordsPerDay(),
                reviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords,
                listOfWords.size()
        );
    }

    private void updateWordForCorrectAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW) || thisWord.getStatus().equals(KNOWN)) {
            if (thisWord.getStatus().equals(NEW)) {
                thisWord.setTotalStreak(5);
            }
            if (thisWord.getStatus().equals(KNOWN)) {
                thisWord.setTotalStreak(thisWord.getTotalStreak() + 1);
            }
            thisWord.setStatus(KNOWN);
            thisWord.setCurrentStreak(0);
            thisWord.setOccurrence(0);
            thisWord.setDateOfLastOccurrence(LocalDate.now());
            listOfWords.remove(thisWord);
        }

        if (thisWord.getStatus().equals(IN_REVIEW)) {
            if ((thisWord.getCurrentStreak() > 0 && thisWord.getCurrentStreak() < 3) ||
                    (thisWord.getCurrentStreak() == 0 && thisWord.getOccurrence() > 1)) {
                thisWord.setCurrentStreak(thisWord.getCurrentStreak() + 1);
                if (thisWord.getCurrentStreak() == 1) {
                    listOfWords.remove(0);
                    listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
                } else {
                    Collections.rotate(listOfWords, -1);
                }
            }
            if (thisWord.getCurrentStreak() == 3 ||
                    (thisWord.getCurrentStreak() == 0 && thisWord.getOccurrence() == 1)) {
                thisWord.setTotalStreak(thisWord.getTotalStreak() + 1);
                if (thisWord.getTotalStreak() >= 5) {
                    thisWord.setStatus(KNOWN);
                }
                thisWord.setCurrentStreak(0);
                thisWord.setOccurrence(0);
                thisWord.setDateOfLastOccurrence(LocalDate.now());
                listOfWords.remove(thisWord);
            }
        }
    }

    private void updateWordForIncorrectAnswer(Word thisWord, List<Word> listOfWords) {
        thisWord.setStatus(IN_REVIEW);
        thisWord.setTotalStreak(0);
        thisWord.setCurrentStreak(0);

        listOfWords.remove(0);
        listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
    }

    @Transactional
    public void updateUserStreak() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleStatistics roleStatistics = roleService.getRoleStatistics();

        Long daysFromLastStreak = DAYS.between(roleStatistics.getDateOfLastStreak(), LocalDate.now());
        Long differenceBetweenRecordStreakAndCurrentStreak = roleStatistics.getRecordStreak() - roleStatistics.getCurrentStreak();

        updateCurrentStreak(roleStatistics, daysFromLastStreak);
        updateRecordStreak(roleStatistics, differenceBetweenRecordStreakAndCurrentStreak, daysFromLastStreak);
        roleStatistics.setDateOfLastStreak(LocalDate.now());

        userRepository.save(user);
    }

    private void updateCurrentStreak(RoleStatistics roleStatistics, Long daysFromLastStreak) {
        if (daysFromLastStreak == 1) {
            roleStatistics.setCurrentStreak(roleStatistics.getCurrentStreak() + 1);
        } else if (daysFromLastStreak > 1) {
            roleStatistics.setCurrentStreak(1L);
        } else if (daysFromLastStreak < 0) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.statistics.updateUserStreak.erroneousCurrentStreak", null, Locale.getDefault()));
        }
    }

    private void updateRecordStreak(RoleStatistics roleStatistics, Long differenceBetweenRecordStreakAndCurrentStreak, Long daysFromLastStreak) {
        if (differenceBetweenRecordStreakAndCurrentStreak == 0 && daysFromLastStreak > 0) {
            roleStatistics.setRecordStreak(roleStatistics.getRecordStreak() + 1);
        } else if (differenceBetweenRecordStreakAndCurrentStreak < 0) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.statistics.updateUserStreak.erroneousCurrentStreak", null, Locale.getDefault()));
        }
    }
}
