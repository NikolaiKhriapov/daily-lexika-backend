package my.project.vocabulary.review;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.exception.ResourceNotFoundException;
import my.project.vocabulary.word.Status;
import my.project.vocabulary.word.Word;
import my.project.vocabulary.word.WordDTO;
import my.project.vocabulary.word.WordDTOMapper;
import my.project.vocabulary.wordpack.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewDTOMapper reviewDTOMapper;
    private final WordDTOMapper wordDTOMapper;
    private final MessageSource messageSource;
    private final WordPackService wordPackService;

    public List<ReviewDTO> getAllReviews() {
        List<Review> allReviews = reviewRepository.findAll();

        List<ReviewDTO> allReviewDTOs = new ArrayList<>();
        for (Review oneReview : allReviews) {
            allReviewDTOs.add(reviewDTOMapper.apply(oneReview));
        }

        return allReviewDTOs;
    }

    public void createReview(ReviewDTO newReviewDTO) {
        WordPack wordPack = wordPackService.getWordPack(newReviewDTO.wordPackName());

        Set<Word> newWords = new HashSet<>(wordPack.getListOfWords()).stream()
                .filter(word -> word.getStatus().equals(Status.NEW))
                .limit(newReviewDTO.maxNewWordsPerDay())
                .collect(Collectors.toSet());
        Set<Word> reviewWords = new HashSet<>(wordPack.getListOfWords()).stream()
                .filter(word -> word.getStatus().equals(Status.IN_REVIEW))
                .limit(newReviewDTO.maxReviewWordsPerDay())
                .collect(Collectors.toSet());

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewWords);

        listOfWords.forEach(word -> {
            word.setOccurrence(0);
            word.setCurrentStreak(0);
        });

        Review newReview = new Review(
                newReviewDTO.maxNewWordsPerDay(),
                newReviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
        );

        reviewRepository.save(newReview);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.delete(getReview(reviewId));
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.review.notFound", null, Locale.getDefault())));
    }

    //TODO: fix
    public void considerAnswer(Long reviewId, String answer) {
        Review review = getReview(reviewId);
        List<Word> listOfWords = review.getListOfWords();

        Word thisWord = listOfWords.get(0);
        thisWord.setOccurrence(thisWord.getOccurrence() + 1);

        if (thisWord.getStatus().equals(Status.NEW)) {
            thisWord.setStatus(Status.IN_REVIEW);
        }

        if (answer.equals("yes")) {
            if ((thisWord.getOccurrence() == 1) || (thisWord.getOccurrence() > 1 && thisWord.getCurrentStreak() == 3)) {
                thisWord.setTotalStreak(thisWord.getTotalStreak() + 1);
                if (thisWord.getTotalStreak() >= 5) {
                    thisWord.setStatus(Status.KNOWN);
                    thisWord.setCurrentStreak(0);
                    thisWord.setOccurrence(0);
                    listOfWords.remove(thisWord);
                }
            } else if (thisWord.getOccurrence() > 1 && thisWord.getCurrentStreak() < 3) {
                thisWord.setCurrentStreak(thisWord.getCurrentStreak() + 1);
            }
            Collections.rotate(listOfWords, -1);
        }
        if (answer.equals("no")) {
            thisWord.setTotalStreak(0);
            thisWord.setCurrentStreak(0);
            thisWord.setStatus(Status.IN_REVIEW);
            listOfWords.remove(thisWord);
            listOfWords.add(3, thisWord);
        }

        review.setListOfWords(listOfWords);
        reviewRepository.save(review);
    }

    public WordDTO showOneReviewWord(Long reviewId) {
        Review review = getReview(reviewId);

        WordDTO wordDTO = null;
        if (review.getListOfWords().size() > 0) {
            Word word = review.getListOfWords().get(0);
            wordDTO = wordDTOMapper.apply(word);
        }

        return wordDTO;
    }
}
