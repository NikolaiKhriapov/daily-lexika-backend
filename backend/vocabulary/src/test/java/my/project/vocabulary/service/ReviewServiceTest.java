package my.project.vocabulary.service;

import my.project.vocabulary.mapper.ReviewMapper;
import my.project.vocabulary.mapper.WordMapper;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.*;
import my.project.vocabulary.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    private ReviewService underTest;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private WordMapper wordMapper;
    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        underTest = new ReviewService(reviewRepository, reviewMapper, wordMapper, messageSource);
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNewInreviewKnown() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(110, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(70, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(30, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNewInreview_InsufficientKnown() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(5, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(110, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(95, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNewKnown_InsufficientInreview() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(5, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(110, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(95, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNew_InsufficientInreviewKnown() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(5, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(5, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(20, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNew_NoInreviewKnown() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(0, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(0, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(10, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientInreview_NoNewKnown() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(0, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(0, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(100, result.size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(100, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientKnown_NoNewInreview() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(0, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(0, Status.IN_REVIEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.KNOWN, 0));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(100, result.size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(100, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    @Test
    void testGenerateListOfWordsForReview__SufficientNewInreviewKnown_InsufficientDateOfLastOccurrence() {
        // Given
        List<Word> wordPackListOfWords = new ArrayList<>();
        wordPackListOfWords.addAll(generateWords(100, Status.NEW, 0));
        wordPackListOfWords.addAll(generateWords(100, Status.IN_REVIEW, 3));
        wordPackListOfWords.addAll(generateWords(100, Status.KNOWN, 10));

        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWords, null);
        Review review = new Review(1L, 10, 100, wordPack, null);
        wordPack.setReview(review);

        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);

        // When
        List<Word> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);

        // Then
        assertEquals(10, result.size());
        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
    }

    private List<Word> generateWords(int quantity, Status status, int totalStreak) {
        List<Word> listOfWords = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            Word word = new Word();
            word.setStatus(status);
            word.setTotalStreak(totalStreak);
            word.setDateOfLastOccurrence(Date.from(Instant.now().minus(0, ChronoUnit.DAYS)));
            listOfWords.add(word);
        }
        return listOfWords;
    }
}