package my.project.services.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.config.DateUtil;
import my.project.config.i18n.I18nUtil;
import my.project.exception.BadRequestException;
import my.project.exception.InternalServerErrorException;
import my.project.exception.ResourceAlreadyExistsException;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dtos.flashcards.ReviewDto;
import my.project.models.dtos.flashcards.ReviewStatisticsDto;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.models.mappers.flashcards.ReviewMapper;
import my.project.models.entities.flashcards.*;
import my.project.repositories.flashcards.ReviewRepository;
import my.project.repositories.user.UserRepository;
import my.project.services.user.RoleService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static my.project.models.entities.enumerations.Status.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordPackService wordPackService;
    private final RoleService roleService;

    @Transactional
    public List<ReviewDto> getAllReviews() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Review> allReviews = reviewRepository.findByUserIdAndWordPack_Platform(user.getId(), platform);

        List<ReviewDto> allReviewDtos = new ArrayList<>();
        for (Review oneReview : allReviews) {
            if (!Objects.equals(oneReview.getDateGenerated().toLocalDate(), DateUtil.nowInUtc().toLocalDate())) {
                reviewRepository.delete(oneReview);
                reviewRepository.save(generateReview(reviewMapper.toDto(oneReview)));
            }
            allReviewDtos.add(reviewMapper.toDto(oneReview));
        }

        return allReviewDtos;
    }

    public ReviewDto updateReview(Long reviewId, ReviewDto reviewDto) {
        Review review = getReview(reviewId);

        List<Word> updatedListOfWords = generateListOfWordsForReview(review.getWordPack(), reviewDto);

        review.setMaxNewWordsPerDay(reviewDto.maxNewWordsPerDay());
        review.setMaxReviewWordsPerDay(reviewDto.maxReviewWordsPerDay());
        review.setListOfWords(updatedListOfWords);
        review.setActualSize(updatedListOfWords.size());

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toDto(updatedReview);
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        throwIfReviewAlreadyExistsByWordPackName(reviewDto.wordPackDto().name());

        Review newReview = reviewRepository.save(generateReview(reviewDto));

        return reviewMapper.toDto(newReview);
    }

    @Transactional
    public ReviewDto refreshReview(Long reviewId) {
        Review review = getReview(reviewId);

        List<Word> updatedListOfWords = generateListOfWordsForReview(review.getWordPack(), reviewMapper.toDto(review));

        review.setListOfWords(updatedListOfWords);
        review.setActualSize(updatedListOfWords.size());

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toDto(updatedReview);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    @Transactional
    public ReviewDto processReviewAction(Long reviewId, Boolean isCorrect) {
        Review review = getReview(reviewId);
        if (isCorrect != null) {
            List<Word> listOfWords = new ArrayList<>(review.getListOfWords());

            Word thisWord = listOfWords.get(0);
            thisWord.setOccurrence((short) (thisWord.getOccurrence() + 1));

            if (isCorrect) {
                updateWordForCorrectAnswer(thisWord, listOfWords);
            }
            if (!isCorrect) {
                updateWordForIncorrectAnswer(thisWord, listOfWords);
            }

            review.setListOfWords(listOfWords);

            if (review.getListOfWords().isEmpty()) {
                review.setDateLastCompleted(DateUtil.nowInUtc());
                updateUserStreak();
            }

            review = reviewRepository.save(review);
        }

        return reviewMapper.toDto(review);
    }

    public ReviewStatisticsDto getReviewStatistics(Long reviewId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        Review review = getReview(reviewId);
        List<Integer> wordDataIds = wordDataService.findAllWordDataIdByWordPackNameAndPlatform(review.getWordPack().getName(), platform);

        Integer newWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.getId(), wordDataIds, NEW);
        Integer reviewWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.getId(), wordDataIds, IN_REVIEW);
        Integer knownWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.getId(), wordDataIds, KNOWN);

        return new ReviewStatisticsDto(
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
    public List<Word> generateListOfWordsForReview(WordPack wordPack, ReviewDto reviewDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        List<Integer> wordDataIds = wordDataService.findAllWordDataIdByWordPackNameAndPlatform(wordPack.getName(), platform);

        if (wordDataIds.isEmpty()) {
            throw new BadRequestException(I18nUtil.getMessage("exceptions.wordPack.noWordData"));
        }

        wordService.updateWordsForUser(user.getId(), wordDataIds);

        List<Word> newWords = wordService.findAllByUserIdAndWordDataIdInAndStatusInRandomLimited(
                user.getId(),
                wordDataIds,
                new ArrayList<>(List.of(NEW)),
                reviewDto.maxNewWordsPerDay()
        );

        List<Word> reviewAndKnownWords = wordService.findAllByUserIdAndWordDataIdInAndStatusInAndPeriodBetweenOrderedLimited(
                user.getId(),
                wordDataIds,
                new ArrayList<>(List.of(IN_REVIEW, KNOWN)),
                reviewDto.maxReviewWordsPerDay()
        );

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewAndKnownWords);

        listOfWords.forEach(word -> {
            word.setOccurrence((short) 0);
            word.setCurrentStreak((short) 0);
        });

        return listOfWords;
    }

    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<Review> allReviewsByUserId = reviewRepository.findByUserIdAndWordPack_Platform(userId, platform);
        reviewRepository.deleteAll(allReviewsByUserId);
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("exceptions.review.notFound")));
    }

    private Review generateReview(ReviewDto reviewDto) {
        Integer userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        WordPack wordPack = wordPackService.findByName(reviewDto.wordPackDto().name());

        List<Word> listOfWords = generateListOfWordsForReview(wordPack, reviewDto);

        return new Review(
                userId,
                reviewDto.maxNewWordsPerDay(),
                reviewDto.maxReviewWordsPerDay(),
                wordPack,
                listOfWords,
                listOfWords.size()
        );
    }

    private void updateWordForCorrectAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW) || thisWord.getStatus().equals(KNOWN)) {
            if (thisWord.getStatus().equals(NEW)) {
                thisWord.setTotalStreak((short) 5);
            }
            if (thisWord.getStatus().equals(KNOWN)) {
                thisWord.setTotalStreak((short) (thisWord.getTotalStreak() + 1));
            }
            thisWord.setStatus(KNOWN);
            thisWord.setCurrentStreak((short) 0);
            thisWord.setOccurrence((short) 0);
            thisWord.setDateOfLastOccurrence(DateUtil.nowInUtc());
            listOfWords.remove(thisWord);
        }

        if (thisWord.getStatus().equals(IN_REVIEW)) {
            if ((thisWord.getCurrentStreak() > 0 && thisWord.getCurrentStreak() < 3) ||
                    (thisWord.getCurrentStreak() == 0 && thisWord.getOccurrence() > 1)) {
                thisWord.setCurrentStreak((short) (thisWord.getCurrentStreak() + 1));
                if (thisWord.getCurrentStreak() == 1) {
                    listOfWords.remove(0);
                    listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
                } else {
                    Collections.rotate(listOfWords, -1);
                }
            }
            if (thisWord.getCurrentStreak() == 3 ||
                    (thisWord.getCurrentStreak() == 0 && thisWord.getOccurrence() == 1)) {
                thisWord.setTotalStreak((short) (thisWord.getTotalStreak() + 1));
                if (thisWord.getTotalStreak() >= 5) {
                    thisWord.setStatus(KNOWN);
                }
                thisWord.setCurrentStreak((short) 0);
                thisWord.setOccurrence((short) 0);
                thisWord.setDateOfLastOccurrence(DateUtil.nowInUtc());
                listOfWords.remove(thisWord);
            }
        }
    }

    private void updateWordForIncorrectAnswer(Word thisWord, List<Word> listOfWords) {
        thisWord.setStatus(IN_REVIEW);
        thisWord.setTotalStreak((short) 0);
        thisWord.setCurrentStreak((short) 0);

        listOfWords.remove(0);
        listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
    }

    private void throwIfReviewAlreadyExistsByWordPackName(String wordPackName) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Platform platform = roleService.getPlatformByRoleName(user.getRole());

        boolean existsReviewByWordPackName = reviewRepository.existsByUserIdAndWordPack_PlatformAndWordPack_Name(user.getId(), platform, wordPackName);
        if (existsReviewByWordPackName) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("exceptions.review.alreadyExists", wordPackName));
        }
    }

    @Transactional
    public void updateUserStreak() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleStatistics roleStatistics = roleService.getRoleStatistics();

        Long daysFromLastStreak = DAYS.between(roleStatistics.getDateOfLastStreak(), DateUtil.nowInUtc());
        Long differenceBetweenRecordStreakAndCurrentStreak = roleStatistics.getRecordStreak() - roleStatistics.getCurrentStreak();

        updateCurrentStreak(roleStatistics, daysFromLastStreak);
        updateRecordStreak(roleStatistics, differenceBetweenRecordStreakAndCurrentStreak, daysFromLastStreak);
        roleStatistics.setDateOfLastStreak(DateUtil.nowInUtc());

        userRepository.save(user);
    }

    private void updateCurrentStreak(RoleStatistics roleStatistics, Long daysFromLastStreak) {
        if (daysFromLastStreak == 1) {
            roleStatistics.setCurrentStreak(roleStatistics.getCurrentStreak() + 1);
        } else if (daysFromLastStreak > 1) {
            roleStatistics.setCurrentStreak(1L);
        } else if (daysFromLastStreak < 0) {
            throw new InternalServerErrorException(I18nUtil.getMessage("exceptions.statistics.updateUserStreak.erroneousCurrentStreak"));
        }
    }

    private void updateRecordStreak(RoleStatistics roleStatistics, Long differenceBetweenRecordStreakAndCurrentStreak, Long daysFromLastStreak) {
        if (differenceBetweenRecordStreakAndCurrentStreak == 0 && daysFromLastStreak > 0) {
            roleStatistics.setRecordStreak(roleStatistics.getRecordStreak() + 1);
        } else if (differenceBetweenRecordStreakAndCurrentStreak < 0) {
            throw new InternalServerErrorException(I18nUtil.getMessage("exceptions.statistics.updateUserStreak.erroneousCurrentStreak"));
        }
    }
}
