package my.project.services.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.exception.ReviewAlreadyExistsException;
import my.project.models.dto.chineseflashcards.ReviewStatisticsDTO;
import my.project.models.mapper.chineseflashcards.ReviewMapper;
import my.project.models.dto.chineseflashcards.ReviewDTO;
import my.project.models.dto.chineseflashcards.WordDTO;
import my.project.models.mapper.chineseflashcards.WordMapper;
import my.project.models.entity.chineseflashcards.*;
import my.project.repositories.chineseflashcards.ReviewRepository;
import my.project.services.user.AuthenticationService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

import static my.project.models.entity.chineseflashcards.Status.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final WordMapper wordMapper;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordPackService wordPackService;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    @Transactional
    public List<ReviewDTO> getAllReviews() {
        Long userId = authenticationService.getAuthenticatedUser().id();
        List<Review> allReviews = reviewRepository.findAllByUserId(userId);

        List<ReviewDTO> allReviewDTOs = new ArrayList<>();
        for (Review oneReview : allReviews) {
            if (!Objects.equals(oneReview.getDateGenerated(), LocalDate.now())) {
                reviewRepository.delete(oneReview);
                reviewRepository.save(generateReview(reviewMapper.toDTO(oneReview), userId));
            }
            allReviewDTOs.add(reviewMapper.toDTO(oneReview));
        }

        return allReviewDTOs;
    }

    public ReviewDTO getReviewById(Long reviewId) {
        return reviewMapper.toDTO(getReview(reviewId));
    }

    public void createReview(ReviewDTO newReviewDTO) {
        Long userId = authenticationService.getAuthenticatedUser().id();

        List<String> wordPackNamesOfExistingReviews = reviewRepository.findAllReviewNamesByUserId(userId);
        if (wordPackNamesOfExistingReviews.contains(newReviewDTO.wordPackName())) {
            throw new ReviewAlreadyExistsException("Review '" + newReviewDTO.wordPackName() + "' already exists");
        }

        Review newReview = generateReview(newReviewDTO, userId);
        reviewRepository.save(newReview);
    }

    @Transactional
    public void refreshReview(Long reviewId) {
        Long userId = authenticationService.getAuthenticatedUser().id();
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
    public Map<String, Object> processReviewAction(Long reviewId, String answer) {
        Review review = getReview(reviewId);
        if (answer != null) {
            List<Word> listOfWords = new ArrayList<>(review.getListOfWords());

            Word thisWord = listOfWords.get(0);
            thisWord.setOccurrence(thisWord.getOccurrence() + 1);

            if (answer.equals("yes")) {
                updateWordForYesAnswer(thisWord, listOfWords);
            }
            if (answer.equals("no")) {
                updateWordForNoAnswer(thisWord, listOfWords);
            }

            review.setListOfWords(listOfWords);
            review = reviewRepository.save(review);
        }

        WordDTO reviewWordDTO = showOneReviewWord(review);

        Map<String, Object> map = null;
        if (reviewWordDTO != null) {
            map = new HashMap<>();
            map.put("reviewWordDTO", reviewWordDTO);
            map.put("reviewUpdatedSize", review.getListOfWords().size());
        }

        return map;
    }

    public List<WordDTO> getAllWordsForReview(Long reviewId) {
        Review review = getReview(reviewId);
        return wordMapper.toDTOShortList(review.getListOfWords());
    }

    public ReviewStatisticsDTO getReviewStatistics(Long reviewId) {
        Long userId = authenticationService.getAuthenticatedUser().id();

        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPack(getReview(reviewId).getWordPack());

        Integer newWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, NEW);
        Integer reviewWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, IN_REVIEW);
        Integer knownWords = wordService.countByUserIdAndWordDataIdInAndStatusEquals(userId, wordDataIds, KNOWN);

        return new ReviewStatisticsDTO(
                newWords,
                reviewWords,
                knownWords,
                wordDataIds.size()
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
        List<Long> wordDataIds = wordDataService.getListOfAllWordDataIdsByWordPack(wordPack);

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

    public void deleteAllByUserId(Long userId) {
        reviewRepository.deleteAllByUserId(userId);
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

    private Review generateReview(ReviewDTO reviewDTO, Long userId) {
        WordPack wordPack = wordPackService.getWordPackByName(reviewDTO.wordPackName());
        List<Word> listOfWords = generateListOfWordsForReview(userId, wordPack, reviewDTO);

        return new Review(
                userId,
                reviewDTO.maxNewWordsPerDay(),
                reviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
        );
    }

    private void updateWordForYesAnswer(Word thisWord, List<Word> listOfWords) {
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

    private void updateWordForNoAnswer(Word thisWord, List<Word> listOfWords) {
        thisWord.setStatus(IN_REVIEW);
        thisWord.setTotalStreak(0);
        thisWord.setCurrentStreak(0);

        listOfWords.remove(0);
        listOfWords.add(Math.min(listOfWords.size(), 3), thisWord);
    }
}
