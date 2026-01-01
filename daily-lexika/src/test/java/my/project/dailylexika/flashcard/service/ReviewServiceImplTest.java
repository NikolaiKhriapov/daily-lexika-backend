package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.Review;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.model.mappers.ReviewMapper;
import my.project.dailylexika.flashcard.persistence.ReviewRepository;
import my.project.dailylexika.flashcard.service.impl.ReviewServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.util.datetime.DateUtil;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.InternalServerErrorException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static my.project.library.dailylexika.enumerations.Status.IN_REVIEW;
import static my.project.library.dailylexika.enumerations.Status.KNOWN;
import static my.project.library.dailylexika.enumerations.Status.NEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ReviewServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;
    private static final String WORD_PACK_NAME = "HSK_1";
    private static final String DESCRIPTION = "Desc";
    private static final Long REVIEW_ID = 10L;

    private ReviewServiceImpl underTest;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private WordService wordService;
    @Mock
    private WordDataService wordDataService;
    @Mock
    private WordPackService wordPackService;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;

    @BeforeEach
    void setUp() {
        underTest = new ReviewServiceImpl(
                reviewRepository,
                reviewMapper,
                wordService,
                wordDataService,
                wordPackService,
                userService,
                roleService
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getAllReviews_returnsList")
    void getAllReviews_returnsList(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.findByUserIdAndWordPack_Platform(USER_ID, platform)).willReturn(List.of(review));
        given(reviewMapper.toDto(review)).willReturn(dto);

        // When
        List<ReviewDto> actual = underTest.getAllReviews();

        // Then
        assertThat(actual).containsExactly(dto);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getAllReviews_refreshesOutdatedReview")
    void getAllReviews_refreshesOutdatedReview(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        review.setDateGenerated(DateUtil.nowInUtc().minusDays(1));
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);

        given(reviewRepository.findByUserIdAndWordPack_Platform(USER_ID, platform)).willReturn(List.of(review));
        given(reviewMapper.toDto(review)).willReturn(dto);
        given(wordDataService.existsByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(true);
        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(1, 2), 1)).willReturn(List.of(buildWord(NEW)));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        List<ReviewDto> actual = underTest.getAllReviews();

        // Then
        assertThat(actual).containsExactly(dto);
        verify(reviewRepository).delete(review);
        verify(reviewRepository).save(any(Review.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getAllReviews_outdatedReviewWithNoWordData")
    void getAllReviews_outdatedReviewWithNoWordData_deletesReview(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review reviewToDelete = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        reviewToDelete.setDateGenerated(DateUtil.nowInUtc().minusDays(1));
        Review reviewToKeep = buildReview(REVIEW_ID + 1, platform, List.of(buildWord(NEW)));
        String otherWordPackName = "HSK_2";
        reviewToKeep.getWordPack().setName(otherWordPackName);
        ReviewDto reviewToKeepDto = new ReviewDto(
                reviewToKeep.getId(),
                USER_ID.longValue(),
                reviewToKeep.getMaxNewWordsPerDay(),
                reviewToKeep.getMaxReviewWordsPerDay(),
                new WordPackDto(otherWordPackName, DESCRIPTION, Category.HSK, platform, null, null, null),
                List.of(),
                reviewToKeep.getActualSize(),
                reviewToKeep.getDateLastCompleted(),
                reviewToKeep.getDateGenerated()
        );

        given(reviewRepository.findByUserIdAndWordPack_Platform(USER_ID, platform)).willReturn(List.of(reviewToDelete, reviewToKeep));
        given(wordDataService.existsByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(false);
        given(reviewMapper.toDto(reviewToKeep)).willReturn(reviewToKeepDto);

        // When
        List<ReviewDto> actual = underTest.getAllReviews();

        // Then
        assertThat(actual).containsExactly(reviewToKeepDto);
        verify(reviewRepository).delete(reviewToDelete);
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).toDto(reviewToDelete);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#createReview_createsNew")
    void createReview_createsNew(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        ReviewDto reviewDto = buildReviewDto(REVIEW_ID, platform, 1, 2);
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));

        given(reviewRepository.existsByUserIdAndWordPack_PlatformAndWordPack_Name(USER_ID, platform, WORD_PACK_NAME)).willReturn(false);
        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(1, 2), 1)).willReturn(List.of(buildWord(NEW)));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        given(reviewMapper.toDto(review)).willReturn(reviewDto);

        // When
        ReviewDto actual = underTest.createReview(reviewDto);

        // Then
        assertThat(actual).isEqualTo(reviewDto);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#createReview_throwIfInvalidInput")
    void createReview_throwIfInvalidInput(ReviewDto reviewDto) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.createReview(reviewDto))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#createReview_throwIfAlreadyExists")
    void createReview_throwIfAlreadyExists(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        ReviewDto reviewDto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.existsByUserIdAndWordPack_PlatformAndWordPack_Name(USER_ID, platform, WORD_PACK_NAME)).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.createReview(reviewDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateReview_updatesFields")
    void updateReview_updatesFields(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review existing = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        ReviewDto updateDto = buildReviewDto(REVIEW_ID, platform, 2, 3);

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(existing));
        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of(buildWord(NEW)));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(1, 2), 3)).willReturn(List.of());
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(1, 2), 3)).willReturn(List.of());
        given(reviewRepository.save(existing)).willReturn(existing);
        given(reviewMapper.toDto(existing)).willReturn(updateDto);

        // When
        ReviewDto actual = underTest.updateReview(REVIEW_ID, updateDto);

        // Then
        assertThat(actual).isEqualTo(updateDto);
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(captor.capture());
        assertThat(captor.getValue().getMaxNewWordsPerDay()).isEqualTo(2);
        assertThat(captor.getValue().getMaxReviewWordsPerDay()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateReview_throwIfInvalidInput")
    void updateReview_throwIfInvalidInput(Long reviewId, ReviewDto reviewDto) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updateReview(reviewId, reviewDto))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateReview_throwIfNotFound")
    void updateReview_throwIfNotFound(Platform platform) {
        // Given
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.updateReview(REVIEW_ID, buildReviewDto(REVIEW_ID, platform, 1, 2)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteReview_deletes")
    void deleteReview_deletes(Platform platform) {
        // Given
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));

        // When
        underTest.deleteReview(REVIEW_ID);

        // Then
        verify(reviewRepository).delete(review);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteReview_throwIfInvalidInput")
    void deleteReview_throwIfInvalidInput(Long reviewId) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteReview(reviewId))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void deleteReview_throwIfNotFound() {
        // Given
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.deleteReview(REVIEW_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getReviewStatistics_returnsCounts")
    void getReviewStatistics_returnsCounts(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2, 3));
        given(wordService.countByUserIdAndWordData_IdInAndStatus(USER_ID, List.of(1, 2, 3), NEW)).willReturn(1);
        given(wordService.countByUserIdAndWordData_IdInAndStatus(USER_ID, List.of(1, 2, 3), IN_REVIEW)).willReturn(2);
        given(wordService.countByUserIdAndWordData_IdInAndStatus(USER_ID, List.of(1, 2, 3), KNOWN)).willReturn(3);

        // When
        ReviewStatisticsDto actual = underTest.getReviewStatistics(REVIEW_ID);

        // Then
        assertThat(actual.reviewId()).isEqualTo(REVIEW_ID);
        assertThat(actual.wordPackName()).isEqualTo(WORD_PACK_NAME);
        assertThat(actual.wordsNew()).isEqualTo(1);
        assertThat(actual.wordsInReview()).isEqualTo(2);
        assertThat(actual.wordsKnown()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getReviewStatistics_throwIfInvalidInput")
    void getReviewStatistics_throwIfInvalidInput(Long reviewId) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getReviewStatistics(reviewId))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#getReviewStatistics_throwIfNotFound")
    void getReviewStatistics_throwIfNotFound(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getReviewStatistics(REVIEW_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#generateListOfWordsForReview_returnsList")
    void generateListOfWordsForReview_returnsList(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        ReviewDto reviewDto = buildReviewDto(REVIEW_ID, platform, 1, 2);
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);
        Word newWord = buildWord(NEW);
        Word reviewWord = buildWord(IN_REVIEW);
        reviewWord.setOccurrence((short) 2);
        reviewWord.setCurrentStreak((short) 2);
        Word knownWord = buildWord(KNOWN);
        knownWord.setOccurrence((short) 1);
        knownWord.setCurrentStreak((short) 1);

        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(1, 2), 1)).willReturn(List.of(newWord));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of(reviewWord));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(1, 2), 1)).willReturn(List.of(knownWord));

        // When
        List<Word> actual = underTest.generateListOfWordsForReview(wordPack, reviewDto);

        // Then
        assertThat(actual).containsExactly(newWord, reviewWord, knownWord);
        assertThat(actual).allMatch(word -> word.getOccurrence() == 0);
        assertThat(actual).allMatch(word -> word.getCurrentStreak() == 0);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#generateListOfWordsForReview_throwIfInvalidInput")
    void generateListOfWordsForReview_throwIfInvalidInput(WordPack wordPack, ReviewDto reviewDto) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.generateListOfWordsForReview(wordPack, reviewDto))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#generateListOfWordsForReview_throwIfNoWordData")
    void generateListOfWordsForReview_throwIfNoWordData(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        ReviewDto reviewDto = buildReviewDto(REVIEW_ID, platform, 1, 2);
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);

        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of());

        // When / Then
        assertThatThrownBy(() -> underTest.generateListOfWordsForReview(wordPack, reviewDto))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#refreshReview_updatesWords")
    void refreshReview_updatesWords(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(reviewMapper.toDto(review)).willReturn(dto);
        given(wordDataService.existsByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(true);
        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(1, 2));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(1, 2), 1)).willReturn(List.of(buildWord(NEW)));
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(wordService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(1, 2), 2)).willReturn(List.of());
        given(reviewRepository.save(review)).willReturn(review);
        given(reviewMapper.toDto(review)).willReturn(dto);

        // When
        Optional<ReviewDto> actual = underTest.refreshReview(REVIEW_ID);

        // Then
        assertThat(actual).contains(dto);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#refreshReview_deletesWhenNoWordData")
    void refreshReview_deletesWhenNoWordData(Platform platform) {
        // Given
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(wordDataService.existsByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(false);

        // When
        Optional<ReviewDto> actual = underTest.refreshReview(REVIEW_ID);

        // Then
        assertThat(actual).isEmpty();
        verify(reviewRepository).delete(review);
        verify(reviewRepository, never()).save(any(Review.class));
        verify(reviewMapper, never()).toDto(review);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#refreshReview_throwIfInvalidInput")
    void refreshReview_throwIfInvalidInput(Long reviewId) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.refreshReview(reviewId))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void refreshReview_throwIfNotFound() {
        // Given
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.refreshReview(REVIEW_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#processReviewAction_returnsDtoWhenNullInput")
    void processReviewAction_returnsDtoWhenNullInput(Platform platform) {
        // Given
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(reviewMapper.toDto(review)).willReturn(dto);

        // When
        ReviewDto actual = underTest.processReviewAction(REVIEW_ID, null);

        // Then
        assertThat(actual).isEqualTo(dto);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#processReviewAction_updatesOnCorrect")
    void processReviewAction_updatesOnCorrect(Platform platform, RoleName roleName) {
        // Given
        Review review = buildReview(REVIEW_ID, platform, new ArrayList<>(List.of(buildWord(NEW))));
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 1L, DateUtil.nowInUtc().minusDays(1), 1L);
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(reviewRepository.save(review)).willReturn(review);
        given(reviewMapper.toDto(review)).willReturn(dto);

        // When
        ReviewDto actual = underTest.processReviewAction(REVIEW_ID, true);

        // Then
        assertThat(actual).isEqualTo(dto);
        assertThat(review.getListOfWords()).isEmpty();
        assertThat(review.getDateLastCompleted()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#processReviewAction_updatesOnIncorrect")
    void processReviewAction_updatesOnIncorrect(Platform platform) {
        // Given
        Word word = buildWord(NEW);
        Review review = buildReview(REVIEW_ID, platform, new ArrayList<>(List.of(word)));
        ReviewDto dto = buildReviewDto(REVIEW_ID, platform, 1, 2);

        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.of(review));
        given(reviewRepository.save(review)).willReturn(review);
        given(reviewMapper.toDto(review)).willReturn(dto);

        // When
        ReviewDto actual = underTest.processReviewAction(REVIEW_ID, false);

        // Then
        assertThat(actual).isEqualTo(dto);
        assertThat(word.getStatus()).isEqualTo(IN_REVIEW);
        assertThat(review.getListOfWords()).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#processReviewAction_throwIfInvalidInput")
    void processReviewAction_throwIfInvalidInput(Long reviewId) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.processReviewAction(reviewId, true))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void processReviewAction_throwIfNotFound() {
        // Given
        given(reviewRepository.findById(REVIEW_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.processReviewAction(REVIEW_ID, true))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateUserStreak_updatesCurrentAndRecord")
    void updateUserStreak_updatesCurrentAndRecord(RoleName roleName) {
        // Given
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 2L, DateUtil.nowInUtc().minusDays(1), 2L);
        given(roleService.getRoleStatistics()).willReturn(roleStats);

        // When
        underTest.updateUserStreak();

        // Then
        verify(userService).updateCurrentStreak(3L);
        verify(userService).updateRecordStreak(3L);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateUserStreak_updatesCurrentOnlyWhenBelowRecord")
    void updateUserStreak_updatesCurrentOnlyWhenBelowRecord(RoleName roleName) {
        // Given
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 2L, DateUtil.nowInUtc().minusDays(1), 5L);
        given(roleService.getRoleStatistics()).willReturn(roleStats);

        // When
        underTest.updateUserStreak();

        // Then
        verify(userService).updateCurrentStreak(3L);
        verify(userService, never()).updateRecordStreak(anyLong());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#updateUserStreak_throwIfErroneousStreak")
    void updateUserStreak_throwIfErroneousStreak(RoleName roleName) {
        // Given
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 3L, DateUtil.nowInUtc().minusDays(1), 1L);
        given(roleService.getRoleStatistics()).willReturn(roleStats);

        // When / Then
        assertThatThrownBy(() -> underTest.updateUserStreak())
                .isInstanceOf(InternalServerErrorException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_deletesAll")
    void deleteAllByUserIdAndPlatform_deletesAll(Platform platform) {
        // Given
        List<Review> reviews = List.of(buildReview(REVIEW_ID, platform, List.of(buildWord(NEW))));
        given(reviewRepository.findByUserIdAndWordPack_Platform(USER_ID, platform)).willReturn(reviews);

        // When
        underTest.deleteAllByUserIdAndPlatform(USER_ID, platform);

        // Then
        verify(reviewRepository).deleteAll(reviews);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_throwIfInvalidInput")
    void deleteAllByUserIdAndPlatform_throwIfInvalidInput(Integer userId, Platform platform) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAllByUserIdAndPlatform(userId, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteReviewIfExistsForWordPack_deletesExisting")
    void deleteReviewIfExistsForWordPack_deletesExisting(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName);
        Review review = buildReview(REVIEW_ID, platform, List.of(buildWord(NEW)));
        given(reviewRepository.findByUserIdAndWordPack_Name(USER_ID, WORD_PACK_NAME)).willReturn(Optional.of(review));

        // When
        underTest.deleteReviewIfExistsForWordPack(WORD_PACK_NAME);

        // Then
        verify(reviewRepository).delete(review);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.ReviewServiceImplTest$TestDataSource#deleteReviewIfExistsForWordPack_throwIfInvalidInput")
    void deleteReviewIfExistsForWordPack_deleteInvalidInputIf(String wordPackName) {
        // Given
        ReviewService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteReviewIfExistsForWordPack(wordPackName))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    private void mockUser(Integer id, RoleName roleName) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
    }

    private Word buildWord(my.project.library.dailylexika.enumerations.Status status) {
        Word word = new Word(USER_ID, null);
        word.setStatus(status);
        word.setOccurrence((short) 1);
        word.setCurrentStreak((short) 1);
        word.setTotalStreak((short) 1);
        word.setDateOfLastOccurrence(DateUtil.nowInUtc().minusDays(1));
        return word;
    }

    private Review buildReview(Long id, Platform platform, List<Word> listOfWords) {
        Review review = new Review(USER_ID, 1, 2, new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform), listOfWords, listOfWords.size());
        review.setId(id);
        review.setDateGenerated(DateUtil.nowInUtc());
        return review;
    }

    private ReviewDto buildReviewDto(Long reviewId, Platform platform, Integer maxNewWords, Integer maxReviewWords) {
        WordPackDto wordPackDto = new WordPackDto(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform, null, null, null);
        return new ReviewDto(reviewId, USER_ID.longValue(), maxNewWords, maxReviewWords, wordPackDto, List.of(), 0, null, null);
    }

    private ReviewService createValidatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        ReviewServiceImpl service = new ReviewServiceImpl(
                reviewRepository,
                reviewMapper,
                wordService,
                wordDataService,
                wordPackService,
                userService,
                roleService
        );
        return (ReviewService) processor.postProcessAfterInitialization(service, "reviewService");
    }

    private static class TestDataSource {

        public static Stream<Arguments> getAllReviews_returnsList() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getAllReviews_refreshesOutdatedReview() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getAllReviews_outdatedReviewWithNoWordData() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createReview_createsNew() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createReview_throwIfInvalidInput() {
            WordPackDto wordPackDto = new WordPackDto(WORD_PACK_NAME, DESCRIPTION, Category.HSK, ENGLISH, null, null, null);
            return Stream.of(
                    arguments((Object) null),
                    arguments(new ReviewDto(null, null, null, 1, wordPackDto, null, null, null, null)),
                    arguments(new ReviewDto(null, null, -1, 1, wordPackDto, null, null, null, null)),
                    arguments(new ReviewDto(null, null, 1, null, wordPackDto, null, null, null, null)),
                    arguments(new ReviewDto(null, null, 1, -1, wordPackDto, null, null, null, null)),
                    arguments(new ReviewDto(null, null, 1, 1, null, null, null, null, null))
            );
        }

        public static Stream<Arguments> createReview_throwIfAlreadyExists() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> updateReview_updatesFields() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> updateReview_throwIfInvalidInput() {
            WordPackDto wordPackDto = new WordPackDto(WORD_PACK_NAME, DESCRIPTION, Category.HSK, ENGLISH, null, null, null);
            return Stream.of(
                    arguments(null, null),
                    arguments(REVIEW_ID, null),
                    arguments(null, new ReviewDto(null, null, 1, 1, wordPackDto, null, null, null, null)),
                    arguments(REVIEW_ID, new ReviewDto(null, null, null, 1, wordPackDto, null, null, null, null)),
                    arguments(REVIEW_ID, new ReviewDto(null, null, -1, 1, wordPackDto, null, null, null, null)),
                    arguments(REVIEW_ID, new ReviewDto(null, null, 1, null, wordPackDto, null, null, null, null)),
                    arguments(REVIEW_ID, new ReviewDto(null, null, 1, -1, wordPackDto, null, null, null, null)),
                    arguments(REVIEW_ID, new ReviewDto(null, null, 1, 1, null, null, null, null, null))
            );
        }

        public static Stream<Arguments> updateReview_throwIfNotFound() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> deleteReview_deletes() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> deleteReview_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getReviewStatistics_returnsCounts() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getReviewStatistics_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getReviewStatistics_throwIfNotFound() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> generateListOfWordsForReview_returnsList() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> generateListOfWordsForReview_throwIfInvalidInput() {
            WordPackDto wordPackDto = new WordPackDto(WORD_PACK_NAME, DESCRIPTION, Category.HSK, ENGLISH, null, null, null);
            return Stream.of(
                    arguments(null, new ReviewDto(null, null, 1, 1, wordPackDto, null, null, null, null)),
                    arguments(new WordPack(), null),
                    arguments(new WordPack(), new ReviewDto(null, null, null, 1, wordPackDto, null, null, null, null)),
                    arguments(new WordPack(), new ReviewDto(null, null, -1, 1, wordPackDto, null, null, null, null)),
                    arguments(new WordPack(), new ReviewDto(null, null, 1, null, wordPackDto, null, null, null, null)),
                    arguments(new WordPack(), new ReviewDto(null, null, 1, -1, wordPackDto, null, null, null, null)),
                    arguments(new WordPack(), new ReviewDto(null, null, 1, 1, null, null, null, null, null))
            );
        }

        public static Stream<Arguments> generateListOfWordsForReview_throwIfNoWordData() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> refreshReview_updatesWords() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> refreshReview_deletesWhenNoWordData() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> refreshReview_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> processReviewAction_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> processReviewAction_returnsDtoWhenNullInput() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> processReviewAction_updatesOnCorrect() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> processReviewAction_updatesOnIncorrect() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> updateUserStreak_updatesCurrentAndRecord() {
            return Stream.of(
                    arguments(USER_ENGLISH),
                    arguments(USER_CHINESE)
            );
        }

        public static Stream<Arguments> updateUserStreak_updatesCurrentOnlyWhenBelowRecord() {
            return Stream.of(
                    arguments(USER_ENGLISH),
                    arguments(USER_CHINESE)
            );
        }

        public static Stream<Arguments> updateUserStreak_throwIfErroneousStreak() {
            return Stream.of(
                    arguments(USER_ENGLISH),
                    arguments(USER_CHINESE)
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_deletesAll() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(null, CHINESE),
                    arguments(USER_ID, null)
            );
        }

        public static Stream<Arguments> deleteReviewIfExistsForWordPack_deletesExisting() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> deleteReviewIfExistsForWordPack_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(""),
                    arguments(" ")
            );
        }
    }
}
