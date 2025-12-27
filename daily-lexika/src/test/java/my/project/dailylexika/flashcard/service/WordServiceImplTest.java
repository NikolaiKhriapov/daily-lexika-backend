package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.mappers.WordMapper;
import my.project.dailylexika.flashcard.persistence.WordRepository;
import my.project.dailylexika.flashcard.service.impl.WordServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.enumerations.Status;
import my.project.library.util.datetime.DateUtil;
import my.project.library.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class WordServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;
    private static final Integer WORD_DATA_ID = 10;
    private static final Integer WORD_DATA_ID_2 = 20;
    private static final Integer WORD_DATA_ID_3 = 30;
    private static final String WORD_PACK_NAME = "HSK_1";

    private WordServiceImpl underTest;
    @Mock
    private WordRepository wordRepository;
    @Mock
    private WordMapper wordMapper;
    @Mock
    private WordDataService wordDataService;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;

    @BeforeEach
    void setUp() {
        underTest = new WordServiceImpl(
                wordRepository,
                wordMapper,
                wordDataService,
                userService,
                roleService
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getByWordDataId_returnsDto")
    void getByWordDataId_returnsDto(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        Word word = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0, platform);
        WordDto expected = new WordDto(1L, user.id(), null, Status.NEW, (short) 0, (short) 0, (short) 0, word.getDateOfLastOccurrence());

        given(wordRepository.findByUserIdAndWordData_Id(user.id(), WORD_DATA_ID)).willReturn(Optional.of(word));
        given(wordMapper.toDto(word)).willReturn(expected);

        // When
        WordDto actual = underTest.getByWordDataId(WORD_DATA_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getByWordDataId_throwIfInvalidInput")
    void getByWordDataId_throwIfInvalidInput(Integer wordDataId) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getByWordDataId(wordDataId))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getByWordDataId_throwIfNotFound")
    void getByWordDataId_throwIfNotFound(RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        given(wordRepository.findByUserIdAndWordData_Id(user.id(), WORD_DATA_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getByWordDataId(WORD_DATA_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByUserIdAndWordDataIdIn_returnsPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Word word1 = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0);
        Word word2 = buildWord(WORD_DATA_ID_2, DateUtil.nowInUtc().minusDays(2), (short) 1);
        Page<Word> page = new PageImpl<>(List.of(word1, word2), pageable, 2);
        WordDto dto1 = new WordDto(1L, USER_ID, null, Status.NEW, (short) 0, (short) 0, (short) 0, word1.getDateOfLastOccurrence());
        WordDto dto2 = new WordDto(2L, USER_ID, null, Status.NEW, (short) 0, (short) 1, (short) 0, word2.getDateOfLastOccurrence());

        given(wordRepository.findByUserIdAndWordDataIdIn(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), pageable)).willReturn(page);
        given(wordMapper.toDto(word1)).willReturn(dto1);
        given(wordMapper.toDto(word2)).willReturn(dto2);

        // When
        Page<WordDto> actual = underTest.getByUserIdAndWordDataIdIn(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), pageable);

        // Then
        assertThat(actual.getContent()).containsExactly(dto1, dto2);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getByUserIdAndWordDataIdIn_throwIfInvalidInput")
    void getByUserIdAndWordDataIdIn_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds, Pageable pageable) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getByUserIdAndWordDataIdIn(userId, wordDataIds, pageable))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getPageByWordPackName_returnsPage")
    void getPageByWordPackName_returnsPage(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        Pageable pageable = PageRequest.of(0, 2);
        Word word1 = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0);
        Word word2 = buildWord(WORD_DATA_ID_2, DateUtil.nowInUtc().minusDays(2), (short) 1);
        Page<Word> page = new PageImpl<>(List.of(word1, word2), pageable, 2);
        WordDto dto1 = new WordDto(1L, user.id(), null, Status.NEW, (short) 0, (short) 0, (short) 0, word1.getDateOfLastOccurrence());
        WordDto dto2 = new WordDto(2L, user.id(), null, Status.NEW, (short) 0, (short) 1, (short) 0, word2.getDateOfLastOccurrence());

        given(wordDataService.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME, platform)).willReturn(List.of(WORD_DATA_ID, WORD_DATA_ID_2));
        given(wordRepository.findByUserIdAndWordDataIdIn(user.id(), List.of(WORD_DATA_ID, WORD_DATA_ID_2), pageable)).willReturn(page);
        given(wordMapper.toDto(word1)).willReturn(dto1);
        given(wordMapper.toDto(word2)).willReturn(dto2);

        // When
        Page<WordDto> actual = underTest.getPageByWordPackName(WORD_PACK_NAME, pageable);

        // Then
        assertThat(actual.getContent()).containsExactly(dto1, dto2);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getPageByWordPackName_throwIfInvalidInput")
    void getPageByWordPackName_throwIfInvalidInput(String wordPackName, Pageable pageable) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getPageByWordPackName(wordPackName, pageable))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getPageByStatus_returnsPage")
    void getPageByStatus_returnsPage(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 0L, null, 0L);
        Pageable pageable = PageRequest.of(0, 2);
        Word word1 = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0);
        Word word2 = buildWord(WORD_DATA_ID_2, DateUtil.nowInUtc().minusDays(2), (short) 1);
        Page<Word> page = new PageImpl<>(List.of(word1, word2), pageable, 2);
        List<WordDto> expected = List.of(
                new WordDto(1L, user.id(), null, Status.NEW, (short) 0, (short) 0, (short) 0, word1.getDateOfLastOccurrence()),
                new WordDto(2L, user.id(), null, Status.NEW, (short) 0, (short) 1, (short) 0, word2.getDateOfLastOccurrence())
        );

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordRepository.findByUserIdAndWordData_PlatformAndStatus(user.id(), platform, Status.NEW, pageable)).willReturn(page);
        given(wordMapper.toDtoList(page.getContent())).willReturn(expected);

        // When
        Page<WordDto> actual = underTest.getPageByStatus(Status.NEW, pageable);

        // Then
        assertThat(actual.getContent()).containsExactlyElementsOf(expected);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getPageByStatus_throwIfInvalidInput")
    void getPageByStatus_throwIfInvalidInput(Status status, Pageable pageable) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getPageByStatus(status, pageable))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_returnsList() {
        // Given
        List<Word> expected = List.of(
                buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0),
                buildWord(WORD_DATA_ID_2, DateUtil.nowInUtc().minusDays(2), (short) 1)
        );
        given(wordRepository.findAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), 2))
                .willReturn(expected);

        // When
        List<Word> actual = underTest.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), 2);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_throwIfInvalidInput")
    void getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds, Integer limit) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited(userId, wordDataIds, limit))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited_filtersAndLimits() {
        // Given
        OffsetDateTime now = DateUtil.nowInUtc();
        Word word1 = buildWord(WORD_DATA_ID, now.minusDays(5), (short) 2);
        Word word2 = buildWord(WORD_DATA_ID_2, now.minusDays(1), (short) 1);
        Word word3 = buildWord(WORD_DATA_ID_3, now.minusDays(10), (short) 0);
        Word word4 = buildWord(WORD_DATA_ID_2, now.minusDays(2), (short) 1);
        List<Word> repositoryResult = List.of(word2, word3, word1, word4);

        given(wordRepository.findAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2, WORD_DATA_ID_3)))
                .willReturn(repositoryResult);

        // When
        List<Word> actual = underTest.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(
                USER_ID,
                List.of(WORD_DATA_ID, WORD_DATA_ID_2, WORD_DATA_ID_3),
                2
        );

        // Then
        assertThat(actual).containsExactly(word4, word1);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited_throwIfInvalidInput")
    void getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds, Integer limit) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited(userId, wordDataIds, limit))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited_filtersAndLimits() {
        // Given
        OffsetDateTime now = DateUtil.nowInUtc();
        Word word1 = buildWord(WORD_DATA_ID, now.minusDays(5), (short) 2);
        Word word2 = buildWord(WORD_DATA_ID_2, now.minusDays(1), (short) 1);
        Word word3 = buildWord(WORD_DATA_ID_3, now.minusDays(10), (short) 0);
        Word word4 = buildWord(WORD_DATA_ID_2, now.minusDays(2), (short) 1);
        List<Word> repositoryResult = List.of(word2, word3, word1, word4);

        given(wordRepository.findAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2, WORD_DATA_ID_3)))
                .willReturn(repositoryResult);

        // When
        List<Word> actual = underTest.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(
                USER_ID,
                List.of(WORD_DATA_ID, WORD_DATA_ID_2, WORD_DATA_ID_3),
                2
        );

        // Then
        assertThat(actual).containsExactly(word3, word1);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited_throwIfInvalidInput")
    void getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds, Integer limit) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited(userId, wordDataIds, limit))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getAllByUserIdAndStatusAndWordData_Platform_returnsList")
    void getAllByUserIdAndStatusAndWordData_Platform_returnsList(Platform platform) {
        // Given
        List<Word> words = List.of(buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0, platform));
        List<WordDto> expected = List.of(new WordDto(1L, USER_ID, null, Status.NEW, (short) 0, (short) 0, (short) 0, words.get(0).getDateOfLastOccurrence()));

        given(wordRepository.findByUserIdAndStatusAndWordData_Platform(USER_ID, Status.NEW, platform)).willReturn(words);
        given(wordMapper.toDtoList(words)).willReturn(expected);

        // When
        List<WordDto> actual = underTest.getAllByUserIdAndStatusAndWordData_Platform(USER_ID, Status.NEW, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
        verify(wordRepository).findByUserIdAndStatusAndWordData_Platform(USER_ID, Status.NEW, platform);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getAllByUserIdAndStatusAndWordData_Platform_throwIfInvalidInput")
    void getAllByUserIdAndStatusAndWordData_Platform_throwIfInvalidInput(Integer userId, Status status, Platform platform) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByUserIdAndStatusAndWordData_Platform(userId, status, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#createAllWordsForUserAndPlatform_createsMissingOnly")
    void createAllWordsForUserAndPlatform_createsMissingOnly(Platform platform) {
        // Given
        List<Integer> allWordDataIds = List.of(WORD_DATA_ID, WORD_DATA_ID_2, WORD_DATA_ID_3);
        Word existing = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0, platform);
        given(wordDataService.getAllWordDataIdByPlatform(platform)).willReturn(allWordDataIds);
        given(wordRepository.findAllByUserId(USER_ID)).willReturn(List.of(existing));
        given(wordDataService.getEntityById(WORD_DATA_ID_2)).willReturn(buildWordData(WORD_DATA_ID_2, platform));
        given(wordDataService.getEntityById(WORD_DATA_ID_3)).willReturn(buildWordData(WORD_DATA_ID_3, platform));

        // When
        underTest.createAllWordsForUserAndPlatform(USER_ID, platform);

        // Then
        ArgumentCaptor<List<Word>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordRepository).saveAll(captor.capture());
        List<Word> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(w -> w.getWordData().getId()).containsExactlyInAnyOrder(WORD_DATA_ID_2, WORD_DATA_ID_3);
        assertThat(saved).allMatch(w -> w.getUserId().equals(USER_ID));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#createAllWordsForUserAndPlatform_throwIfInvalidInput")
    void createAllWordsForUserAndPlatform_throwIfInvalidInput(Integer userId, Platform platform) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.createAllWordsForUserAndPlatform(userId, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#updateWordsForUser_createsMissingOnly")
    void updateWordsForUser_createsMissingOnly(Platform platform) {
        // Given
        List<Integer> wordDataIds = List.of(WORD_DATA_ID, WORD_DATA_ID_2);
        Word existing = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0, platform);
        given(wordRepository.findByUserIdAndWordDataIdIn(USER_ID, wordDataIds)).willReturn(List.of(existing));
        given(wordDataService.getEntityById(WORD_DATA_ID_2)).willReturn(buildWordData(WORD_DATA_ID_2, platform));

        // When
        underTest.updateWordsForUser(USER_ID, wordDataIds);

        // Then
        ArgumentCaptor<List<Word>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordRepository).saveAll(captor.capture());
        List<Word> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getWordData().getId()).isEqualTo(WORD_DATA_ID_2);
        assertThat(saved.get(0).getUserId()).isEqualTo(USER_ID);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#updateWordsForUser_throwIfInvalidInput")
    void updateWordsForUser_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updateWordsForUser(userId, wordDataIds))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void deleteAllByWordDataId_deletesEach() {
        // Given
        List<Integer> wordDataIds = List.of(WORD_DATA_ID, WORD_DATA_ID_2);

        // When
        underTest.deleteAllByWordDataId(wordDataIds);

        // Then
        verify(wordRepository).deleteAllByWordData_Id(WORD_DATA_ID);
        verify(wordRepository).deleteAllByWordData_Id(WORD_DATA_ID_2);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#deleteAllByWordDataId_throwIfInvalidInput")
    void deleteAllByWordDataId_throwIfInvalidInput(List<Integer> wordDataIds) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAllByWordDataId(wordDataIds))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_deletesAll")
    void deleteAllByUserIdAndPlatform_deletesAll(Platform platform) {
        // Given
        List<Word> words = List.of(buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0, platform));
        given(wordRepository.findByUserIdAndWordData_Platform(USER_ID, platform)).willReturn(words);

        // When
        underTest.deleteAllByUserIdAndPlatform(USER_ID, platform);

        // Then
        verify(wordRepository).deleteAll(words);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_throwIfInvalidInput")
    void deleteAllByUserIdAndPlatform_throwIfInvalidInput(Integer userId, Platform platform) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAllByUserIdAndPlatform(userId, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getWordOfTheDay_returnsDto")
    void getWordOfTheDay_returnsDto(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 0L, null, 0L);
        Word word = buildWord(WORD_DATA_ID, DateUtil.nowInUtc().minusDays(1), (short) 0);
        WordDto expected = new WordDto(1L, user.id(), null, Status.NEW, (short) 0, (short) 0, (short) 0, word.getDateOfLastOccurrence());

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordDataService.getIdByWordOfTheDayDateAndPlatform(platform)).willReturn(WORD_DATA_ID);
        given(wordRepository.findByUserIdAndWordData_Id(user.id(), WORD_DATA_ID)).willReturn(Optional.of(word));
        given(wordMapper.toDto(word)).willReturn(expected);

        // When
        WordDto actual = underTest.getWordOfTheDay();

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#getWordOfTheDay_throwIfNotFound")
    void getWordOfTheDay_throwIfNotFound(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName);
        RoleStatisticsDto roleStats = new RoleStatisticsDto(1L, roleName, 0L, null, 0L);

        given(roleService.getRoleStatistics()).willReturn(roleStats);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(wordDataService.getIdByWordOfTheDayDateAndPlatform(platform)).willReturn(WORD_DATA_ID);
        given(wordRepository.findByUserIdAndWordData_Id(user.id(), WORD_DATA_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getWordOfTheDay())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void countByUserIdAndWordData_IdInAndStatus_returnsCount() {
        // Given
        given(wordRepository.countByUserIdAndWordData_IdInAndStatus(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), Status.NEW)).willReturn(5);

        // When
        Integer actual = underTest.countByUserIdAndWordData_IdInAndStatus(USER_ID, List.of(WORD_DATA_ID, WORD_DATA_ID_2), Status.NEW);

        // Then
        assertThat(actual).isEqualTo(5);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordServiceImplTest$TestDataSource#countByUserIdAndWordData_IdInAndStatus_throwIfInvalidInput")
    void countByUserIdAndWordData_IdInAndStatus_throwIfInvalidInput(Integer userId, List<Integer> wordDataIds, Status status) {
        // Given
        WordService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.countByUserIdAndWordData_IdInAndStatus(userId, wordDataIds, status))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    private UserDto mockUser(Integer id, RoleName roleName) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
        return user;
    }

    private Word buildWord(Integer wordDataId, OffsetDateTime dateOfLastOccurrence, short totalStreak) {
        return buildWord(wordDataId, dateOfLastOccurrence, totalStreak, ENGLISH);
    }

    private Word buildWord(Integer wordDataId, OffsetDateTime dateOfLastOccurrence, short totalStreak, Platform platform) {
        WordData wordData = buildWordData(wordDataId, platform);
        Word word = new Word(USER_ID, wordData);
        word.setDateOfLastOccurrence(dateOfLastOccurrence);
        word.setTotalStreak(totalStreak);
        return word;
    }

    private WordData buildWordData(Integer id, Platform platform) {
        WordData wordData = new WordData();
        wordData.setId(id);
        wordData.setPlatform(platform);
        return wordData;
    }

    private WordService createValidatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        WordServiceImpl service = new WordServiceImpl(
                wordRepository,
                wordMapper,
                wordDataService,
                userService,
                roleService
        );
        return (WordService) processor.postProcessAfterInitialization(service, "wordService");
    }

    private static class TestDataSource {

        public static Stream<Arguments> getByWordDataId_returnsDto() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getByWordDataId_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getByWordDataId_throwIfNotFound() {
            return Stream.of(
                    arguments(USER_ENGLISH),
                    arguments(USER_CHINESE)
            );
        }

        public static Stream<Arguments> getByUserIdAndWordDataIdIn_throwIfInvalidInput() {
            Pageable pageable = PageRequest.of(0, 1);
            List<Integer> ids = List.of(WORD_DATA_ID);
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, ids, pageable),
                    arguments(USER_ID, null, pageable),
                    arguments(USER_ID, ids, null)
            );
        }

        public static Stream<Arguments> getPageByWordPackName_returnsPage() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getPageByWordPackName_throwIfInvalidInput() {
            Pageable pageable = PageRequest.of(0, 1);
            return Stream.of(
                    arguments(null, null),
                    arguments(null, pageable),
                    arguments("", pageable),
                    arguments(" ", pageable),
                    arguments("\t", pageable),
                    arguments("\n", pageable),
                    arguments(WORD_PACK_NAME, null)
            );
        }

        public static Stream<Arguments> getPageByStatus_returnsPage() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getPageByStatus_throwIfInvalidInput() {
            Pageable pageable = PageRequest.of(0, 1);
            return Stream.of(
                    arguments(null, null),
                    arguments(null, pageable),
                    arguments(Status.NEW, null)
            );
        }

        public static Stream<Arguments> getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_throwIfInvalidInput() {
            List<Integer> ids = List.of(WORD_DATA_ID);
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, ids, 1),
                    arguments(USER_ID, null, 1),
                    arguments(USER_ID, ids, null)
            );
        }

        public static Stream<Arguments> getAllByUserIdAndWordDataIdInAndStatusInReviewAndPeriodBetweenOrderedDescLimited_throwIfInvalidInput() {
            return getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_throwIfInvalidInput();
        }

        public static Stream<Arguments> getAllByUserIdAndWordDataIdInAndStatusKnownAndPeriodBetweenOrderedAscLimited_throwIfInvalidInput() {
            return getAllByUserIdAndWordDataIdInAndStatusNewRandomLimited_throwIfInvalidInput();
        }

        public static Stream<Arguments> getAllByUserIdAndStatusAndWordData_Platform_returnsList() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllByUserIdAndStatusAndWordData_Platform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, Status.NEW, ENGLISH),
                    arguments(null, Status.IN_REVIEW, CHINESE),
                    arguments(null, Status.KNOWN, ENGLISH),
                    arguments(USER_ID, null, ENGLISH),
                    arguments(USER_ID, null, CHINESE),
                    arguments(USER_ID, Status.NEW, null)
            );
        }

        public static Stream<Arguments> createAllWordsForUserAndPlatform_createsMissingOnly() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> createAllWordsForUserAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, null),
                    arguments(null, ENGLISH),
                    arguments(null, CHINESE),
                    arguments(USER_ID, null)
            );
        }

        public static Stream<Arguments> updateWordsForUser_createsMissingOnly() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> updateWordsForUser_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, null),
                    arguments(null, List.of(WORD_DATA_ID)),
                    arguments(USER_ID, null)
            );
        }

        public static Stream<Arguments> deleteAllByWordDataId_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
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
                    arguments(null, null),
                    arguments(null, ENGLISH),
                    arguments(null, CHINESE),
                    arguments(USER_ID, null)
            );
        }

        public static Stream<Arguments> getWordOfTheDay_returnsDto() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getWordOfTheDay_throwIfNotFound() {
            return getWordOfTheDay_returnsDto();
        }

        public static Stream<Arguments> countByUserIdAndWordData_IdInAndStatus_throwIfInvalidInput() {
            List<Integer> ids = List.of(WORD_DATA_ID);
            return Stream.of(
                    arguments(null, null, null),
                    arguments(null, ids, Status.NEW),
                    arguments(null, ids, Status.IN_REVIEW),
                    arguments(null, ids, Status.KNOWN),
                    arguments(USER_ID, null, Status.NEW),
                    arguments(USER_ID, null, Status.IN_REVIEW),
                    arguments(USER_ID, null, Status.KNOWN),
                    arguments(USER_ID, ids, null)
            );
        }
    }
}
