package my.project.vocabulary.service;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.exception.ResourceNotFoundException;
import my.project.vocabulary.exception.ReviewAlreadyExistsException;
import my.project.vocabulary.mapper.ReviewMapper;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Status;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.dto.WordDTO;
import my.project.vocabulary.mapper.WordMapper;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.repository.ReviewRepository;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static my.project.vocabulary.model.entity.Status.*;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final WordMapper wordMapper;
    private final MessageSource messageSource;

    public List<ReviewDTO> getAllReviews() {
        List<Review> allReviews = reviewRepository.findAll();

        List<ReviewDTO> allReviewDTOs = new ArrayList<>();
        for (Review oneReview : allReviews) {
            allReviewDTOs.add(reviewMapper.toDTO(oneReview));
        }

        return allReviewDTOs;
    }

    @Transactional
    public void createReview(ReviewDTO newReviewDTO) {
        List<Review> existingReviews = reviewRepository.findAll();
        if (existingReviews
                .stream().map(review -> review.getWordPack().getName()).toList()
                .contains(newReviewDTO.wordPackName())) {
            throw new ReviewAlreadyExistsException("Review '" + newReviewDTO.wordPackName() + "' already exists");
        }

        Review newReview = reviewMapper.toEntity(newReviewDTO);
        reviewRepository.save(newReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.review.notFound", null, Locale.getDefault())));
    }

    @Transactional
    public void processReviewAction(Long reviewId, String answer) {
        if (answer != null) {
            Review review = getReview(reviewId);
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
            reviewRepository.save(review);
        }
    }

    @Transactional
    public WordDTO showOneReviewWord(Long reviewId) {
        Review review = getReview(reviewId);
        if (!review.getListOfWords().isEmpty()) {
            Word word = review.getListOfWords().get(0);
            return wordMapper.toDTO(word);
        }
        return null;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateReviews() {
        List<Review> existingReviews = reviewRepository.findAll();
        reviewRepository.deleteAll(existingReviews);

        existingReviews.forEach(oneReview -> reviewRepository.save(reviewMapper.toEntity(reviewMapper.toDTO(oneReview))));
    }

    private static void updateWordForNoAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW)) {
            thisWord.setStatus(IN_REVIEW);
        }

        thisWord.setTotalStreak(0);
        thisWord.setCurrentStreak(0);
        thisWord.setStatus(IN_REVIEW);
        listOfWords.remove(0);
        listOfWords.add(3, thisWord);
    }

    private static void updateWordForYesAnswer(Word thisWord, List<Word> listOfWords) {
        if (thisWord.getStatus().equals(NEW) || thisWord.getStatus().equals(KNOWN)) {
            thisWord.setStatus(KNOWN);
            thisWord.setCurrentStreak(0);
            thisWord.setOccurrence(0);
            thisWord.setDateOfLastOccurrence(new Date());
            listOfWords.remove(thisWord);
        }

        if (thisWord.getStatus().equals(IN_REVIEW)) {
            if (thisWord.getCurrentStreak() < 3) {
                thisWord.setCurrentStreak(thisWord.getCurrentStreak() + 1);
            }
            if (thisWord.getCurrentStreak() == 3) {
                thisWord.setTotalStreak(thisWord.getTotalStreak() + 1);
                if (thisWord.getTotalStreak() >= 5) {
                    thisWord.setStatus(KNOWN);
                }
                thisWord.setCurrentStreak(0);
                thisWord.setOccurrence(0);
                thisWord.setDateOfLastOccurrence(new Date());
                listOfWords.remove(thisWord);
            }
        }

        Collections.rotate(listOfWords, -1);
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
    public List<Word> generateListOfWordsForReview(WordPack wordPack, ReviewDTO reviewDTO) {
        List<Word> newWords = wordPack.getListOfWords().stream()
                .filter(word -> word.getStatus().equals(Status.NEW))
                .limit(reviewDTO.maxNewWordsPerDay())
                .toList();
        List<Word> reviewWords = wordPack.getListOfWords().stream()
                .filter(word -> word.getStatus().equals(Status.IN_REVIEW))
                .filter(word -> (Duration.between(
                        word.getDateOfLastOccurrence().toInstant(),
                        Instant.now()
                ).toDays()) >= word.getTotalStreak() * 2)
                .sorted(Comparator.comparing(Word::getDateOfLastOccurrence).reversed())
                .limit(reviewDTO.maxReviewWordsPerDay())
                .toList();
        List<Word> knownWords = wordPack.getListOfWords().stream()
                .filter(word -> word.getStatus().equals(Status.KNOWN))
                .filter(word -> Duration.between(
                        word.getDateOfLastOccurrence().toInstant(),
                        Instant.now()
                ).toDays() >= word.getTotalStreak())
                .sorted(Comparator.comparing(Word::getDateOfLastOccurrence).reversed())
                .limit(reviewDTO.maxReviewWordsPerDay())
                .toList();

        int totalReviewWords = reviewWords.size();
        int totalKnownWords = knownWords.size();

        boolean totalReviewWordsIsEnough = totalReviewWords > reviewDTO.maxReviewWordsPerDay() * 0.7;
        boolean totalKnownWordsIsEnough = totalKnownWords > reviewDTO.maxReviewWordsPerDay() * 0.3;

        int selectedReviewWords;
        int selectedKnownWords;

        if (totalReviewWordsIsEnough && totalKnownWordsIsEnough) {
            selectedReviewWords = (int) (reviewDTO.maxReviewWordsPerDay() * 0.7);
            selectedKnownWords = reviewDTO.maxReviewWordsPerDay() - selectedReviewWords;
        } else if (totalReviewWordsIsEnough) {
            selectedKnownWords = totalKnownWords;
            selectedReviewWords = reviewDTO.maxReviewWordsPerDay() - selectedKnownWords;
        } else if (totalKnownWordsIsEnough) {
            selectedReviewWords = totalReviewWords;
            selectedKnownWords = reviewDTO.maxReviewWordsPerDay() - selectedReviewWords;
        } else {
            selectedReviewWords = totalReviewWords;
            selectedKnownWords = totalKnownWords;
        }

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewWords.stream().limit(selectedReviewWords).toList());
        listOfWords.addAll(knownWords.stream().limit(selectedKnownWords).toList());

        listOfWords.forEach(word -> {
            word.setOccurrence(0);
            word.setCurrentStreak(0);
        });

        return listOfWords;
    }
}
