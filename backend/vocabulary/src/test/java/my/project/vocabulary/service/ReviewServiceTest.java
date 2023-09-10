package my.project.vocabulary.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewMapperTest {

//    private ReviewMapper underTest;
//    @Mock
//    private WordPackService wordPackService;
//
//    @BeforeEach
//    void setUp() {
//        underTest = new ReviewMapper(wordPackService);
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNewInreviewKnown() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(110, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(70, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(30, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNewInreview_InsufficientKnown() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(5, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(110, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(95, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNewKnown_InsufficientInreview() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(5, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(110, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(95, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNew_InsufficientInreviewKnown() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(5, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(5, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(20, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(5, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNew_NoInreviewKnown() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(0, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(0, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(10, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientInreview_NoNewKnown() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(0, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(0, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(100, result.size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(100, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientKnown_NoNewInreview() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(0, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(0, Status.IN_REVIEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.KNOWN, 0));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(100, result.size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(100, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    @Test
//    void testGenerateListOfWordsForReview__SufficientNewInreviewKnown_InsufficientDateOfLastOccurrence() {
//        // Given
//        List<WordData> wordPackListOfWordData = new ArrayList<>();
//        wordPackListOfWordData.addAll(generateWords(100, Status.NEW, 0));
//        wordPackListOfWordData.addAll(generateWords(100, Status.IN_REVIEW, 3));
//        wordPackListOfWordData.addAll(generateWords(100, Status.KNOWN, 10));
//
//        WordPack wordPack = new WordPack("TestWordPack", null, Category.HSK, wordPackListOfWordData, null);
//        Review review = new Review(1L, 10, 100, wordPack, null);
//        wordPack.setReview(review);
//
//        ReviewDTO reviewDTO = new ReviewDTO(1L, 10, 100, "TestWordPack", null);
//
//        // When
//        List<WordData> result = underTest.generateListOfWordsForReview(wordPack, reviewDTO);
//
//        // Then
//        assertEquals(10, result.size());
//        assertEquals(10, result.stream().filter(word -> word.getStatus().equals(Status.NEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.IN_REVIEW)).toList().size());
//        assertEquals(0, result.stream().filter(word -> word.getStatus().equals(Status.KNOWN)).toList().size());
//    }
//
//    private List<WordData> generateWords(int quantity, Status status, int totalStreak) {
//        List<WordData> listOfWordData = new ArrayList<>();
//        for (int i = 1; i <= quantity; i++) {
//            WordData wordData = new WordData();
//            wordData.setStatus(status);
//            wordData.setTotalStreak(totalStreak);
//            wordData.setDateOfLastOccurrence(Date.from(Instant.now().minus(0, ChronoUnit.DAYS)));
//            listOfWordData.add(wordData);
//        }
//        return listOfWordData;
//    }
}