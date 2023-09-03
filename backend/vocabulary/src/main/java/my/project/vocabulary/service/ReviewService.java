package my.project.vocabulary.service;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.exception.ResourceNotFoundException;
import my.project.vocabulary.mapper.ReviewMapper;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.dto.WordDTO;
import my.project.vocabulary.mapper.WordMapper;
import my.project.vocabulary.repository.ReviewRepository;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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
                listOfWords.remove(thisWord);
            }
        }

        Collections.rotate(listOfWords, -1);
    }
}
