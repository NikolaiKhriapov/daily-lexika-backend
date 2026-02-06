package my.project.dailylexika.flashcard.service;

import jakarta.validation.ConstraintViolationException;
import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.flashcard.service.impl.WordDataServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.events.flashcard.WordDataToBeDeletedEvent;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceNotFoundException;
import my.project.dailylexika.util.ValidationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WordDataServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;
    private static final Integer WORD_DATA_ID = 10;
    private static final String WORD_PACK_NAME = "Custom";
    private static final String WORD_PACK_NAME_ORIGINAL = "HSK_1";
    private static final Long WORD_PACK_ID = 1L;
    private static final Long WORD_PACK_ID_ORIGINAL = 2L;
    private static final String DESCRIPTION = "Description";

    private WordDataServiceImpl underTest;
    @Mock
    private WordDataRepository wordDataRepository;
    @Mock
    private WordDataMapper wordDataMapper;
    @Mock
    private WordPackService wordPackService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        underTest = new WordDataServiceImpl(
                wordDataRepository,
                wordDataMapper,
                wordPackService,
                userService,
                roleService,
                eventPublisher,
                objectMapper
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#search_returnsDtos")
    void search_returnsDtos(Platform platform, RoleName roleName, Language translationLanguage) {
        // Given
        UserDto user = new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), translationLanguage, null, null);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform);

        given(userService.getUser()).willReturn(user);
        given(roleService.getPlatformByRoleName(user.role())).willReturn(platform);
        given(wordDataRepository.searchByPlatformAndQuery(eq(platform), eq(translationLanguage), eq(false), anyString(), anyString(), any()))
                .willReturn(new PageImpl<>(List.of(wordData)));
        given(wordDataMapper.toDtoList(List.of(wordData))).willReturn(List.of(expected));

        // When
        List<WordDataDto> actual = underTest.search("  hello  ", 5);

        // Then
        assertThat(actual).containsExactly(expected);
        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> transcriptionCaptor = ArgumentCaptor.forClass(String.class);
        verify(wordDataRepository).searchByPlatformAndQuery(eq(platform), eq(translationLanguage), eq(false),
                queryCaptor.capture(), transcriptionCaptor.capture(), any());
        assertThat(queryCaptor.getValue()).isEqualTo("hello");
        assertThat(transcriptionCaptor.getValue()).isNotBlank();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#search_throwIfInvalidInput")
    void search_throwIfInvalidInput(String query, Integer limit) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.search(query, limit))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByWordPackIdAndPlatform_returnsAll")
    void getAllByWordPackIdAndPlatform_returnsAll(Platform platform) {
        // Given
        List<WordData> expected = List.of(buildWordData(WORD_DATA_ID, new ArrayList<>(), platform));
        given(wordDataRepository.findAllByListOfWordPacks_IdAndPlatform(WORD_PACK_ID_ORIGINAL, platform)).willReturn(expected);

        // When
        List<WordData> actual = underTest.getAllByWordPackIdAndPlatform(WORD_PACK_ID_ORIGINAL, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByWordPackIdAndPlatform_throwIfInvalidInput")
    void getAllByWordPackIdAndPlatform_throwIfInvalidInput(Long wordPackId, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByWordPackIdAndPlatform(wordPackId, platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#existsByWordPackIdAndPlatform_returns")
    void existsByWordPackIdAndPlatform_returns(Platform platform, Long count, boolean expected) {
        // Given
        given(wordDataRepository.countByListOfWordPacks_IdAndPlatform(WORD_PACK_ID_ORIGINAL, platform)).willReturn(count);

        // When
        boolean actual = underTest.existsByWordPackIdAndPlatform(WORD_PACK_ID_ORIGINAL, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#existsByWordPackIdAndPlatform_throwIfInvalidInput")
    void existsByWordPackIdAndPlatform_throwIfInvalidInput(Long wordPackId, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.existsByWordPackIdAndPlatform(wordPackId, platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_addsWhenMissing")
    void addCustomWordPackToWordData_addsWhenMissing(Platform platform, RoleName roleName) {
        // Given
        given(userService.getUser()).willReturn(new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), null, null, null));
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(
                new WordPack(WORD_PACK_ID_ORIGINAL, WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform, null)
        )), platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class))).willReturn(expected);

        // When
        WordDataDto actual = underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).contains(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfInvalidInput")
    void addCustomWordPackToWordData_throwIfInvalidInput(Integer wordDataId, Long wordPackId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.addCustomWordPackToWordData(wordDataId, wordPackId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfAlreadyAdded")
    void addCustomWordPackToWordData_throwIfAlreadyAdded(Platform platform, RoleName roleName) {
        // Given
        given(userService.getUser()).willReturn(new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), null, null, null));
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(wordPack)), platform);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_ID))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfNotCustom")
    void addCustomWordPackToWordData_throwIfNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform, null);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPack);

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_ID))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_removesWhenPresent")
    void removeCustomWordPackFromWordData_removesWhenPresent(Platform platform, RoleName roleName) {
        // Given
        given(userService.getUser()).willReturn(new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), null, null, null));
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        List<WordPack> packs = new ArrayList<>();
        packs.add(wordPack);
        packs.add(new WordPack(WORD_PACK_ID_ORIGINAL, WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform, null));
        WordData wordData = buildWordData(WORD_DATA_ID, packs, platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class))).willReturn(expected);

        // When
        WordDataDto actual = underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).doesNotContain(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfInvalidInput")
    void removeCustomWordPackFromWordData_throwIfInvalidInput(Integer wordDataId, Long wordPackId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.removeCustomWordPackFromWordData(wordDataId, wordPackId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfNotPresent")
    void removeCustomWordPackFromWordData_throwIfNotPresent(Platform platform, RoleName roleName) {
        // Given
        given(userService.getUser()).willReturn(new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), null, null, null));
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));

        // When / Then
        assertThatThrownBy(() -> underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_ID))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfNotCustom")
    void removeCustomWordPackFromWordData_throwIfNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform, null);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPack);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPack);

        // When / Then
        assertThatThrownBy(() -> underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_ID))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackId_addsToAll")
    void addCustomWordPackToWordDataByWordPackId_addsToAll(Platform platform, RoleName roleName) {
        // Given
        mockUser(1, roleName, platform);
        WordPack wordPackToBeAdded = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        WordPack originalPack = new WordPack(WORD_PACK_ID_ORIGINAL, WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform, null);

        WordData first = buildWordData(10, new ArrayList<>(List.of(originalPack)), platform);
        WordData second = buildWordData(20, new ArrayList<>(List.of(originalPack, wordPackToBeAdded)), platform);
        List<WordData> wordDataList = new ArrayList<>(List.of(first, second));

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPackToBeAdded);
        given(wordPackService.getById(WORD_PACK_ID_ORIGINAL)).willReturn(originalPack);
        given(wordDataRepository.findAllByListOfWordPacks_IdAndPlatform(WORD_PACK_ID_ORIGINAL, platform)).willReturn(wordDataList);

        // When
        underTest.addCustomWordPackToWordDataByWordPackId(WORD_PACK_ID, WORD_PACK_ID_ORIGINAL);

        // Then
        ArgumentCaptor<List<WordData>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordDataRepository).saveAll(captor.capture());
        List<WordData> saved = captor.getValue();
        assertThat(saved.get(0).getListOfWordPacks()).contains(wordPackToBeAdded);
        assertThat(saved.get(1).getListOfWordPacks()).containsExactlyInAnyOrder(originalPack, wordPackToBeAdded);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackId_throwIfInvalidInput")
    void addCustomWordPackToWordDataByWordPackId_throwIfInvalidInput(Long toBeAdded, Long original) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.addCustomWordPackToWordDataByWordPackId(toBeAdded, original))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackId_throwIfNotCustom")
    void addCustomWordPackToWordDataByWordPackId_throwIfNotCustom(Platform platform, RoleName roleName) {
        // Given
        mockUser(1, roleName, platform);
        WordPack wordPackToBeAdded = new WordPack(WORD_PACK_ID, WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform, null);

        given(wordPackService.getById(WORD_PACK_ID)).willReturn(wordPackToBeAdded);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPackToBeAdded);

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordDataByWordPackId(WORD_PACK_ID, WORD_PACK_ID_ORIGINAL))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).saveAll(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeWordPackReferences_removesAndSaves")
    void removeWordPackReferences_removesAndSaves(Platform platform) {
        // Given
        WordPack removePack = new WordPack(1L, "A1", DESCRIPTION, Category.HSK, platform, null);
        WordPack keepPack = new WordPack(2L, "A2", DESCRIPTION, Category.HSK, platform, null);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(removePack, keepPack)), platform);

        given(wordDataRepository.findAllByListOfWordPacks_IdAndPlatform(removePack.getId(), platform))
                .willReturn(List.of(wordData));

        // When
        underTest.removeWordPackReferences(removePack.getId(), platform);

        // Then
        ArgumentCaptor<List<WordData>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordDataRepository).saveAll(captor.capture());
        assertThat(captor.getValue().get(0).getListOfWordPacks()).containsExactly(keepPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeWordPackReferences_throwIfInvalidInput")
    void removeWordPackReferences_throwIfInvalidInput(Long wordPackId, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.removeWordPackReferences(wordPackId, platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getPage_withQuery_usesSearch")
    void getPage_withQuery_usesSearch(Platform platform) {
        // Given
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        given(wordDataRepository.searchByPlatformAndQuery(eq(platform), eq(null), eq(true), anyString(), anyString(), any()))
                .willReturn(new PageImpl<>(List.of(wordData)));
        given(wordDataMapper.toDto(wordData))
                .willReturn(new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform));

        // When
        underTest.getPage(platform, "hello", PageRequest.of(0, 10));

        // Then
        verify(wordDataRepository, never()).findAllByPlatform(eq(platform), any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getPage_noQuery_sortsByPlatform")
    void getPage_noQuery_sortsByPlatform(Platform platform, String sortProperty) {
        // Given
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        given(wordDataRepository.findAllByPlatform(eq(platform), any()))
                .willReturn(new PageImpl<>(List.of(wordData)));
        given(wordDataMapper.toDto(wordData))
                .willReturn(new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform));

        // When
        underTest.getPage(platform, null, PageRequest.of(0, 10));

        // Then
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(wordDataRepository).findAllByPlatform(eq(platform), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor(sortProperty)).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getPage_throwIfInvalidInput")
    void getPage_throwIfInvalidInput(Platform platform, Pageable pageable) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getPage(platform, null, pageable))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getById_returnsDto")
    void getById_returnsDto(Platform platform) {
        // Given
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));
        given(wordDataMapper.toDto(wordData)).willReturn(expected);

        // When
        WordDataDto actual = underTest.getById(WORD_DATA_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getById_throwIfInvalidInput")
    void getById_throwIfInvalidInput(Integer wordDataId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getById(wordDataId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getById_throwIfNotFound() {
        // Given
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getById(WORD_DATA_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#create_assignsNextIdAndWordOfTheDayDate")
    void create_assignsNextIdAndWordOfTheDayDate(Platform platform, Long wordPackId, List<Integer> existingIds, Integer expectedId, LocalDate latestDate, WordDataCreateDto createDto) {
        // Given
        WordPack wordPack = new WordPack(wordPackId, "Pack", DESCRIPTION, Category.HSK, platform, null);

        given(wordPackService.getById(wordPackId)).willReturn(wordPack);
        given(wordDataRepository.findAllWordDataIdsByPlatformOrderByIdAsc(platform)).willReturn(existingIds);
        given(wordDataRepository.findMaxWordOfTheDayDateByPlatform(platform)).willReturn(latestDate);
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class)))
                .willReturn(new WordDataDto(expectedId, null, null, null, null, null, null, null, null, platform));

        // When
        underTest.create(createDto);

        // Then
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        WordData saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(expectedId);
        assertThat(saved.getWordOfTheDayDate()).isEqualTo(latestDate.plusDays(1));
        assertThat(saved.getListOfWordPacks()).contains(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#create_throwIfInvalidInput")
    void create_throwIfInvalidInput(WordDataCreateDto input) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.create(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#create_throwIfWordPackCustom")
    void create_throwIfWordPackCustom(Platform platform, Long wordPackId, WordDataCreateDto createDto) {
        // Given
        WordPack wordPack = new WordPack(wordPackId, "Custom", DESCRIPTION, Category.CUSTOM, platform, USER_ID);

        given(wordPackService.getById(wordPackId)).willReturn(wordPack);
        given(wordDataRepository.findAllWordDataIdsByPlatformOrderByIdAsc(platform))
                .willReturn(List.of(platform == ENGLISH ? 3_000_001 : 2_000_001));
        given(wordDataRepository.findMaxWordOfTheDayDateByPlatform(platform)).willReturn(LocalDate.of(2024, 1, 1));

        // When / Then
        assertThatThrownBy(() -> underTest.create(createDto))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#update_updatesProvidedFields")
    void update_updatesProvidedFields(Platform platform) {
        // Given
        WordData existing = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        WordDataUpdateDto patchDto = new WordDataUpdateDto(null, null, "new", null, null, null, null);

        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(existing));
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class)))
                .willReturn(new WordDataDto(WORD_DATA_ID, null, null, "new", null, null, null, null, null, platform));

        // When
        underTest.update(WORD_DATA_ID, patchDto);

        // Then
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getNameEnglish()).isEqualTo("new");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#update_setsWordPacksWhenProvided")
    void update_setsWordPacksWhenProvided(Platform platform) {
        // Given
        WordPack first = new WordPack(1L, "A1", DESCRIPTION, Category.HSK, platform, null);
        WordPack second = new WordPack(2L, "A2", DESCRIPTION, Category.HSK, platform, null);
        WordData existing = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(first)), platform);
        WordDataUpdateDto patchDto = new WordDataUpdateDto(null, null, null, null, null, null, List.of(first.getId(), second.getId()));

        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(existing));
        given(wordPackService.getById(first.getId())).willReturn(first);
        given(wordPackService.getById(second.getId())).willReturn(second);
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class)))
                .willReturn(new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform));

        // When
        underTest.update(WORD_DATA_ID, patchDto);

        // Then
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).containsExactlyInAnyOrder(first, second);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#update_preservesCustomWordPacks")
    void update_preservesCustomWordPacks(Platform platform) {
        // Given
        WordPack first = new WordPack(1L, "A1", DESCRIPTION, Category.HSK, platform, null);
        WordPack second = new WordPack(2L, "A2", DESCRIPTION, Category.HSK, platform, null);
        WordPack custom = new WordPack(3L, "Custom", DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        WordData existing = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(first, custom)), platform);
        WordDataUpdateDto patchDto = new WordDataUpdateDto(null, null, null, null, null, null, List.of(first.getId(), second.getId()));

        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(existing));
        given(wordPackService.getById(first.getId())).willReturn(first);
        given(wordPackService.getById(second.getId())).willReturn(second);
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class)))
                .willReturn(new WordDataDto(WORD_DATA_ID, null, null, null, null, null, null, null, null, platform));

        // When
        underTest.update(WORD_DATA_ID, patchDto);

        // Then
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).containsExactlyInAnyOrder(first, second, custom);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#update_throwIfInvalidInput")
    void update_throwIfInvalidInput(Integer wordDataId, WordDataUpdateDto input) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.update(wordDataId, input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#update_throwIfInvalidNameEnglish")
    void update_throwIfInvalidNameEnglish(Platform platform, String invalidValue) {
        // Given
        WordData existing = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        WordDataUpdateDto patchDto = new WordDataUpdateDto(null, null, invalidValue, null, null, null, null);

        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(existing));

        // When / Then
        assertThatThrownBy(() -> underTest.update(WORD_DATA_ID, patchDto))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#delete_publishesEventAndDeletes")
    void delete_publishesEventAndDeletes(Platform platform) {
        // Given
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));

        // When
        underTest.delete(WORD_DATA_ID);

        // Then
        ArgumentCaptor<WordDataToBeDeletedEvent> eventCaptor = ArgumentCaptor.forClass(WordDataToBeDeletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().wordDataId()).isEqualTo(WORD_DATA_ID);
        verify(wordDataRepository).deleteAllWordPackLinksByWordDataId(WORD_DATA_ID);
        ArgumentCaptor<List<WordData>> deleteCaptor = ArgumentCaptor.forClass(List.class);
        verify(wordDataRepository).deleteAll(deleteCaptor.capture());
        assertThat(deleteCaptor.getValue()).containsExactly(wordData);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#delete_throwIfInvalidInput")
    void delete_throwIfInvalidInput(Integer wordDataId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.delete(wordDataId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByPlatform_returnsIds")
    void getAllWordDataIdByPlatform_returnsIds(Platform platform) {
        // Given
        List<Integer> expected = List.of(1, 2);
        given(wordDataRepository.findAllWordDataIdsByPlatform(platform)).willReturn(expected);

        // When
        List<Integer> actual = underTest.getAllWordDataIdByPlatform(platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByPlatform_throwIfInvalidInput")
    void getAllWordDataIdByPlatform_throwIfInvalidInput(Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllWordDataIdByPlatform(platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByWordPackIdAndPlatform_returnsIds")
    void getAllWordDataIdByWordPackIdAndPlatform_returnsIds(Platform platform) {
        // Given
        List<Integer> expected = List.of(1, 2);
        given(wordDataRepository.findAllWordDataIdsByWordPackIdAndPlatform(WORD_PACK_ID_ORIGINAL, platform)).willReturn(expected);

        // When
        List<Integer> actual = underTest.getAllWordDataIdByWordPackIdAndPlatform(WORD_PACK_ID_ORIGINAL, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByWordPackIdAndPlatform_throwIfInvalidInput")
    void getAllWordDataIdByWordPackIdAndPlatform_throwIfInvalidInput(Long wordPackId, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllWordDataIdByWordPackIdAndPlatform(wordPackId, platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getIdByWordOfTheDayDateAndPlatform_returnsId")
    void getIdByWordOfTheDayDateAndPlatform_returnsId(Platform platform) {
        // Given
        given(wordDataRepository.findIdByWordOfTheDayDateAndPlatform(any(LocalDate.class), eq(platform)))
                .willReturn(Optional.of(10));

        // When
        Integer actual = underTest.getIdByWordOfTheDayDateAndPlatform(platform);

        // Then
        assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getIdByWordOfTheDayDateAndPlatform_throwIfInvalidInput")
    void getIdByWordOfTheDayDateAndPlatform_throwIfInvalidInput(Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getIdByWordOfTheDayDateAndPlatform(platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getIdByWordOfTheDayDateAndPlatform_throwIfNotFound")
    void getIdByWordOfTheDayDateAndPlatform_throwIfNotFound(Platform platform) {
        // Given
        given(wordDataRepository.findIdByWordOfTheDayDateAndPlatform(any(LocalDate.class), eq(platform)))
                .willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getIdByWordOfTheDayDateAndPlatform(platform))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getEntityById_returnsEntity")
    void getEntityById_returnsEntity(Platform platform) {
        // Given
        WordData expected = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(expected));

        // When
        WordData actual = underTest.getEntityById(WORD_DATA_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getEntityById_throwIfInvalidInput")
    void getEntityById_throwIfInvalidInput(Integer wordDataId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getEntityById(wordDataId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getEntityById_throwIfNotFound() {
        // Given
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getEntityById(WORD_DATA_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private WordData buildWordData(Integer id, List<WordPack> wordPacks, Platform platform) {
        return new WordData(id, "cn", "tr", "en", "ru", "def", "ex", wordPacks, LocalDate.now(), platform);
    }

    private void mockUser(Integer id, RoleName roleName, Platform platform) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
    }

    private WordDataService createValidatedService() {
        WordDataServiceImpl service = new WordDataServiceImpl(
                wordDataRepository,
                wordDataMapper,
                wordPackService,
                userService,
                roleService,
                eventPublisher,
                objectMapper
        );
        return ValidationTestSupport.validatedProxy(service, "wordDataService", WordDataService.class);
    }

    private static class TestDataSource {

        public static Stream<Arguments> getAllByWordPackIdAndPlatform_returnsAll() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllByWordPackIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(WORD_PACK_ID_ORIGINAL, null)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_addsWhenMissing() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, WORD_PACK_ID),
                    arguments(1, null)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfAlreadyAdded() {
            return addCustomWordPackToWordData_addsWhenMissing();
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfNotCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_removesWhenPresent() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, WORD_PACK_ID),
                    arguments(1, null)
            );
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfNotPresent() {
            return removeCustomWordPackFromWordData_removesWhenPresent();
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfNotCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackId_addsToAll() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackId_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, WORD_PACK_ID_ORIGINAL),
                    arguments(WORD_PACK_ID, null)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackId_throwIfNotCustom() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> removeWordPackReferences_removesAndSaves() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> removeWordPackReferences_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(WORD_PACK_ID, null)
            );
        }

        public static Stream<Arguments> getPage_withQuery_usesSearch() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getPage_noQuery_sortsByPlatform() {
            return Stream.of(
                    arguments(ENGLISH, "nameEnglish"),
                    arguments(CHINESE, "nameChinese")
            );
        }

        public static Stream<Arguments> getPage_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, PageRequest.of(0, 10)),
                    arguments(ENGLISH, null)
            );
        }

        public static Stream<Arguments> create_assignsNextIdAndWordOfTheDayDate() {
            Map<String, String> chineseExample = Map.of("ch", "你好", "en", "Hello", "ru", "привет");
            List<Map<String, String>> chineseExamples = List.of(chineseExample, chineseExample, chineseExample, chineseExample, chineseExample);

            Map<String, String> englishExample = Map.of("ch", "我爱你", "pinyin", "wo ai ni", "en", "I love you", "ru", "я люблю тебя");
            List<Map<String, String>> englishExamples = List.of(englishExample, englishExample, englishExample, englishExample, englishExample);

            return Stream.of(
                    arguments(ENGLISH, 10L, List.of(3_000_001, 3_000_002, 3_000_004), 3_000_003, LocalDate.of(2024, 1, 1), new WordDataCreateDto("你好", "/helo/", "hello", "привет", "Greeting.", chineseExamples, List.of(10L), ENGLISH)),
                    arguments(CHINESE, 20L, List.of(2_000_001, 2_000_003), 2_000_002, LocalDate.of(2024, 1, 1), new WordDataCreateDto("爱", "ai", "love", "любовь", "含义", englishExamples, List.of(20L), CHINESE))
            );
        }

        public static Stream<Arguments> create_throwIfWordPackCustom() {
            Map<String, String> chineseExample = Map.of("ch", "你好", "en", "Hello", "ru", "привет");
            List<Map<String, String>> chineseExamples = List.of(chineseExample, chineseExample, chineseExample, chineseExample, chineseExample);

            Map<String, String> englishExample = Map.of("ch", "我爱你", "pinyin", "wo ai ni", "en", "I love you", "ru", "я люблю тебя");
            List<Map<String, String>> englishExamples = List.of(englishExample, englishExample, englishExample, englishExample, englishExample);

            return Stream.of(
                    arguments(ENGLISH, 11L, new WordDataCreateDto("你好", "/helo/", "hello", "привет", "Greeting.", chineseExamples, List.of(11L), ENGLISH)),
                    arguments(CHINESE, 21L, new WordDataCreateDto("爱", "ai", "love", "любовь", "含义", englishExamples, List.of(21L), CHINESE))
            );
        }

        public static Stream<Arguments> update_updatesProvidedFields() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> update_setsWordPacksWhenProvided() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> update_preservesCustomWordPacks() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> update_throwIfInvalidNameEnglish() {
            return Stream.of(
                    arguments(ENGLISH, "bad name"),
                    arguments(CHINESE, "bad;name")
            );
        }

        public static Stream<Arguments> delete_publishesEventAndDeletes() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getById_returnsDto() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllWordDataIdByPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getAllWordDataIdByPlatform_returnsIds() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllWordDataIdByWordPackIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(WORD_PACK_ID_ORIGINAL, null)
            );
        }

        public static Stream<Arguments> getAllWordDataIdByWordPackIdAndPlatform_returnsIds() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getIdByWordOfTheDayDateAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getIdByWordOfTheDayDateAndPlatform_returnsId() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getIdByWordOfTheDayDateAndPlatform_throwIfNotFound() {
            return getIdByWordOfTheDayDateAndPlatform_returnsId();
        }

        public static Stream<Arguments> getEntityById_returnsEntity() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getEntityById_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getById_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> search_returnsDtos() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, Language.RUSSIAN),
                    arguments(CHINESE, USER_CHINESE, Language.ENGLISH)
            );
        }

        public static Stream<Arguments> search_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, 1),
                    arguments("", 1),
                    arguments(" ", 1),
                    arguments("query", null),
                    arguments("query", 0),
                    arguments("query", -1)
            );
        }

        public static Stream<Arguments> existsByWordPackIdAndPlatform_returns() {
            return Stream.of(
                    arguments(ENGLISH, 1L, true),
                    arguments(CHINESE, 0L, false)
            );
        }

        public static Stream<Arguments> existsByWordPackIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(WORD_PACK_ID_ORIGINAL, null)
            );
        }

        public static Stream<Arguments> create_throwIfInvalidInput() {
            List<Map<String, String>> examples = List.of(Map.of("ch", "你好", "en", "Hello", "ru", "привет"));
            return Stream.of(
                    arguments((Object) null),
                    arguments(new WordDataCreateDto(null, "/helo/", "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("", "/helo/", "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto(" ", "/helo/", "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", null, "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "", "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", null, "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", null, "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "", "Greeting.", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "привет", null, examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "привет", "", examples, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "привет", "Greeting.", null, List.of(WORD_PACK_ID), ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "привет", "Greeting.", examples, null, ENGLISH)),
                    arguments(new WordDataCreateDto("你好", "/helo/", "hello", "привет", "Greeting.", examples, List.of(WORD_PACK_ID), null))
            );
        }

        public static Stream<Arguments> update_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, new WordDataUpdateDto(null, null, "new", null, null, null, null)),
                    arguments(WORD_DATA_ID, null),
                    arguments(null, null)
            );
        }

        public static Stream<Arguments> delete_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }
    }
}
