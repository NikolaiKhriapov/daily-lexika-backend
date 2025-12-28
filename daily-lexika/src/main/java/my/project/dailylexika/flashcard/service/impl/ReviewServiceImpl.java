package my.project.dailylexika.flashcard.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.util.datetime.DateUtil;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.dailylexika.flashcard.model.mappers.ReviewMapper;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.InternalServerErrorException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static my.project.library.dailylexika.enumerations.Status.*;

@Service
@RequiredArgsConstructor
@Validated
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordPackService wordPackService;
    private final PublicUserService userService;
    private final PublicRoleService roleService;

    @Override
    @Transactional
    public List<ReviewDto> getAllReviews() {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        List<Review> allReviews = reviewRepository.findByUserIdAndWordPack_Platform(user.id(), platform);

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

    @Override
    @Transactional
    public ReviewDto createReview(ReviewDto reviewDto) {
        throwIfReviewAlreadyExistsByWordPackName(reviewDto.wordPackDto().name());
        Review newReview = reviewRepository.save(generateReview(reviewDto));
        return reviewMapper.toDto(newReview);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsDto getReviewStatistics(Long reviewId) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        Review review = getReview(reviewId);
        List<Integer> wordDataIds = wordDataService.getAllWordDataIdByWordPackNameAndPlatform(review.getWordPack().getName(), platform);

        Integer newWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.id(), wordDataIds, NEW);
        Integer reviewWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.id(), wordDataIds, IN_REVIEW);
        Integer knownWords = wordService.countByUserIdAndWordData_IdInAndStatus(user.id(), wordDataIds, KNOWN);

        return new ReviewStatisticsDto(
                review.getId(),
                review.getWordPack().getName(),
                newWords,
                reviewWords,
                knownWords
        );
    }

    @Override
    @Transactional
    public List<Word> generateListOfWordsForReview(WordPack wordPack, ReviewDto reviewDto) {
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());
        List<Integer> wordDataIds = getWordDataIdsForWordPack(wordPack, platform);
        wordService.updateWordsForUser(user.id(), wordDataIds);
        return fetchWordsForReview(user.id(), wordDataIds, reviewDto);
    }

    @Override
    @Transactional
    public ReviewDto refreshReview(Long reviewId) {
        Review review = getReview(reviewId);

        List<Word> updatedListOfWords = generateListOfWordsForReview(review.getWordPack(), reviewMapper.toDto(review));

        review.setListOfWords(updatedListOfWords);
        review.setActualSize(updatedListOfWords.size());

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toDto(updatedReview);
    }

    @Override
    @Transactional
    public ReviewDto processReviewAction(Long reviewId, Boolean isCorrect) {
        Review review = getReview(reviewId);

        if (isCorrect == null) {
            return reviewMapper.toDto(review);
        }

        processAnswer(review, isCorrect);
        checkAndUpdateReviewCompletion(review);

        review = reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public void updateUserStreak() {
        RoleStatisticsDto roleStatistics = roleService.getRoleStatistics();

        Long daysFromLastStreak = DAYS.between(roleStatistics.dateOfLastStreak(), DateUtil.nowInUtc());
        Long differenceBetweenRecordStreakAndCurrentStreak = roleStatistics.recordStreak() - roleStatistics.currentStreak();

        updateCurrentStreak(roleStatistics, daysFromLastStreak);
        updateRecordStreak(roleStatistics, differenceBetweenRecordStreakAndCurrentStreak, daysFromLastStreak);
    }

    @Override
    public void deleteAllByUserIdAndPlatform(Integer userId, Platform platform) {
        List<Review> allReviewsByUserId = reviewRepository.findByUserIdAndWordPack_Platform(userId, platform);
        reviewRepository.deleteAll(allReviewsByUserId);
    }

    @Override
    public void deleteReviewIfExistsForWordPack(String wordPackName) {
        Integer userId = userService.getUser().id();

        Optional<Review> review = reviewRepository.findByUserIdAndWordPack_Name(userId, wordPackName);
        review.ifPresent(reviewRepository::delete);
    }

    /**
     * Rules for answering in Daily Reviews:
     * - When NEW –> YES –> KNOWN
     * - When NEW -> NO  -> IN_REVIEW
     * - When KNOWN –> YES -> KNOWN
     * - When KNOWN -> NO  -> IN_REVIEW
     * - When IN_REVIEW(0-3) -> YES -> IN_REVIEW(1-4)
     * - When IN_REVIEW(4) -> YES -> KNOWN
     */
    private void processAnswer(Review review, boolean isCorrect) {
        List<Word> listOfWords = new ArrayList<>(review.getListOfWords());

        Word currentWord = listOfWords.get(0);
        currentWord.setOccurrence((short) (currentWord.getOccurrence() + 1));

        if (isCorrect) {
            updateWordForCorrectAnswer(currentWord, listOfWords);
        } else {
            updateWordForIncorrectAnswer(currentWord, listOfWords);
        }

        review.setListOfWords(listOfWords);
    }

    private void checkAndUpdateReviewCompletion(Review review) {
        if (review.getListOfWords().isEmpty()) {
            review.setDateLastCompleted(DateUtil.nowInUtc());
            updateUserStreak();
        }
    }

    private List<Integer> getWordDataIdsForWordPack(WordPack wordPack, Platform platform) {
        List<Integer> wordDataIds = wordDataService.getAllWordDataIdByWordPackNameAndPlatform(wordPack.getName(), platform);
        if (wordDataIds.isEmpty()) {
            throw new BadRequestException(I18nUtil.getMessage("dailylexika-exceptions.wordPack.noWordData"));
        }
        return wordDataIds;
    }

    /**
     * Rules for generating a Daily Review:
     * - First, the NEW words are added in the amount of review.maxNewWordsPerDay
     * - Then, the IN_REVIEW words are added in the amount of review.maxReviewWordsPerDay * 0.7
     * - Then, the KNOWN words are added in the amount of review.maxReviewWordsPerDay * 0.3
     * - When it is not enough of the IN_REVIEW words, it is compensated by the KNOWN words, and vice versa
     * IN_REVIEW -> if dateOfLastOccurrence >= totalStreak x2
     * KNOWN -> if dateOfLastOccurrence >= totalStreak
     */
    private List<Word> fetchWordsForReview(Integer userId, List<Integer> wordDataIds, ReviewDto reviewDto) {
        List<Word> newWords = wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(
                userId,
                wordDataIds,
                reviewDto.maxNewWordsPerDay()
        );
        List<Word> reviewWords = wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(
                userId,
                wordDataIds,
                reviewDto.maxReviewWordsPerDay()
        );
        List<Word> knownWords = wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(
                userId,
                wordDataIds,
                reviewDto.maxReviewWordsPerDay() - reviewWords.size()
        );

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewWords);
        listOfWords.addAll(knownWords);

        listOfWords.forEach(word -> {
            word.setOccurrence((short) 0);
            word.setCurrentStreak((short) 0);
        });

        return listOfWords;
    }

    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.review.notFound")));
    }

    private Review generateReview(ReviewDto reviewDto) {
        Integer userId = userService.getUser().id();
        WordPack wordPack = wordPackService.getByName(reviewDto.wordPackDto().name());

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
                if (thisWord.getTotalStreak() < 7) {
                    thisWord.setTotalStreak((short) (thisWord.getTotalStreak() + 1));
                }
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
        UserDto user = userService.getUser();
        Platform platform = roleService.getPlatformByRoleName(user.role());

        boolean existsReviewByWordPackName = reviewRepository.existsByUserIdAndWordPack_PlatformAndWordPack_Name(user.id(), platform, wordPackName);
        if (existsReviewByWordPackName) {
            throw new ResourceAlreadyExistsException(I18nUtil.getMessage("dailylexika-exceptions.review.alreadyExists", wordPackName));
        }
    }

    private void updateCurrentStreak(RoleStatisticsDto roleStatistics, Long daysFromLastStreak) {
        if (daysFromLastStreak == 1) {
            userService.updateCurrentStreak(roleStatistics.currentStreak() + 1);
        } else if (daysFromLastStreak > 1) {
            userService.updateCurrentStreak(1L);
        } else if (daysFromLastStreak < 0) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.statistics.updateUserStreak.erroneousCurrentStreak"));
        }
    }

    private void updateRecordStreak(RoleStatisticsDto roleStatistics, Long differenceBetweenRecordStreakAndCurrentStreak, Long daysFromLastStreak) {
        if (differenceBetweenRecordStreakAndCurrentStreak == 0 && daysFromLastStreak > 0) {
            userService.updateRecordStreak(roleStatistics.recordStreak() + 1);
        } else if (differenceBetweenRecordStreakAndCurrentStreak < 0) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.statistics.updateUserStreak.erroneousCurrentStreak"));
        }
    }
}
