package my.project.vocabulary.mapper;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Status;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.service.WordPackService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    private final WordPackService wordPackService;

    @Override
    public ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                entity.getWordPack().getName(),
                entity.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Review toEntity(ReviewDTO reviewDTO) {
        WordPack wordPack = wordPackService.getWordPack(reviewDTO.wordPackName());
        List<Word> listOfWords = generateListOfWordsForReview(wordPack, reviewDTO);

        return new Review(
                reviewDTO.maxNewWordsPerDay(),
                reviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
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
