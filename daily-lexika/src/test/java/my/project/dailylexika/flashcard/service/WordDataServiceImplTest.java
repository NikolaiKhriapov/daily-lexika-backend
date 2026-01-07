package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.model.mappers.WordDataMapper;
import my.project.dailylexika.flashcard.persistence.WordDataRepository;
import my.project.dailylexika.flashcard.service.impl.WordDataServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.util.exception.BadRequestException;
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

import java.time.LocalDate;
import java.util.ArrayList;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WordDataServiceImplTest extends AbstractUnitTest {

    private static final Integer WORD_DATA_ID = 10;
    private static final String WORD_PACK_NAME = "EN__Custom__1";
    private static final String WORD_PACK_NAME_ORIGINAL = "HSK_1";
    private static final String DESCRIPTION = "Description";

    private WordDataServiceImpl underTest;
    @Mock
    private WordDataRepository wordDataRepository;
    @Mock
    private WordDataMapper wordDataMapper;
    @Mock
    private WordPackService wordPackService;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;

    @BeforeEach
    void setUp() {
        underTest = new WordDataServiceImpl(
                wordDataRepository,
                wordDataMapper,
                wordPackService,
                userService,
                roleService
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByPlatform_returnsAll")
    void getAllByPlatform_returnsAll(Platform platform) {
        // Given
        List<WordData> expected = List.of(buildWordData(WORD_DATA_ID, new ArrayList<>(), platform));
        given(wordDataRepository.findAllByPlatform(platform)).willReturn(expected);

        // When
        List<WordData> actual = underTest.getAllByPlatform(platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByPlatform_throwIfInvalidInput")
    void getAllByPlatform_throwIfInvalidInput(Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByPlatform(platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByWordPackNameAndPlatform_returnsAll")
    void getAllByWordPackNameAndPlatform_returnsAll(Platform platform) {
        // Given
        List<WordData> expected = List.of(buildWordData(WORD_DATA_ID, new ArrayList<>(), platform));
        given(wordDataRepository.findAllByListOfWordPacks_NameAndPlatform(WORD_PACK_NAME_ORIGINAL, platform)).willReturn(expected);

        // When
        List<WordData> actual = underTest.getAllByWordPackNameAndPlatform(WORD_PACK_NAME_ORIGINAL, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllByWordPackNameAndPlatform_throwIfInvalidInput")
    void getAllByWordPackNameAndPlatform_throwIfInvalidInput(String name, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllByWordPackNameAndPlatform(name, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_addsWhenMissing")
    void addCustomWordPackToWordData_addsWhenMissing(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(new WordPack(WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform))), platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID.longValue(), null, null, null, null, null, null, null, null, platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class))).willReturn(expected);

        // When
        WordDataDto actual = underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_NAME);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).contains(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfInvalidInput")
    void addCustomWordPackToWordData_throwIfInvalidInput(Integer wordDataId, String name) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.addCustomWordPackToWordData(wordDataId, name))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfAlreadyAdded")
    void addCustomWordPackToWordData_throwIfAlreadyAdded(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(List.of(wordPack)), platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_NAME))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordData_throwIfNotCustom")
    void addCustomWordPackToWordData_throwIfNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPack);

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordData(WORD_DATA_ID, WORD_PACK_NAME))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_removesWhenPresent")
    void removeCustomWordPackFromWordData_removesWhenPresent(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform);
        List<WordPack> packs = new ArrayList<>();
        packs.add(wordPack);
        packs.add(new WordPack(WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform));
        WordData wordData = buildWordData(WORD_DATA_ID, packs, platform);
        WordDataDto expected = new WordDataDto(WORD_DATA_ID.longValue(), null, null, null, null, null, null, null, null, platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));
        given(wordDataRepository.save(any(WordData.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(wordDataMapper.toDto(any(WordData.class))).willReturn(expected);

        // When
        WordDataDto actual = underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_NAME);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<WordData> captor = ArgumentCaptor.forClass(WordData.class);
        verify(wordDataRepository).save(captor.capture());
        assertThat(captor.getValue().getListOfWordPacks()).doesNotContain(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfInvalidInput")
    void removeCustomWordPackFromWordData_throwIfInvalidInput(Integer wordDataId, String name) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.removeCustomWordPackFromWordData(wordDataId, name))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfNotPresent")
    void removeCustomWordPackFromWordData_throwIfNotPresent(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform);
        WordData wordData = buildWordData(WORD_DATA_ID, new ArrayList<>(), platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(wordData));

        // When / Then
        assertThatThrownBy(() -> underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_NAME))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).save(any(WordData.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#removeCustomWordPackFromWordData_throwIfNotCustom")
    void removeCustomWordPackFromWordData_throwIfNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPack);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPack);

        // When / Then
        assertThatThrownBy(() -> underTest.removeCustomWordPackFromWordData(WORD_DATA_ID, WORD_PACK_NAME))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackName_addsToAll")
    void addCustomWordPackToWordDataByWordPackName_addsToAll(Platform platform, RoleName roleName) {
        // Given
        mockUser(1, roleName, platform);
        WordPack wordPackToBeAdded = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.CUSTOM, platform);
        WordPack originalPack = new WordPack(WORD_PACK_NAME_ORIGINAL, DESCRIPTION, Category.HSK, platform);

        WordData first = buildWordData(10, new ArrayList<>(List.of(originalPack)), platform);
        WordData second = buildWordData(20, new ArrayList<>(List.of(originalPack, wordPackToBeAdded)), platform);
        List<WordData> wordDataList = new ArrayList<>(List.of(first, second));

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPackToBeAdded);
        given(wordDataRepository.findAllByListOfWordPacks_NameAndPlatform(WORD_PACK_NAME_ORIGINAL, platform)).willReturn(wordDataList);

        // When
        underTest.addCustomWordPackToWordDataByWordPackName(WORD_PACK_NAME, WORD_PACK_NAME_ORIGINAL);

        // Then
        ArgumentCaptor<List<WordData>> captor = ArgumentCaptor.forClass(List.class);
        verify(wordDataRepository).saveAll(captor.capture());
        List<WordData> saved = captor.getValue();
        assertThat(saved.get(0).getListOfWordPacks()).contains(wordPackToBeAdded);
        assertThat(saved.get(1).getListOfWordPacks()).containsExactlyInAnyOrder(originalPack, wordPackToBeAdded);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackName_throwIfInvalidInput")
    void addCustomWordPackToWordDataByWordPackName_throwIfInvalidInput(String toBeAdded, String original) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.addCustomWordPackToWordDataByWordPackName(toBeAdded, original))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#addCustomWordPackToWordDataByWordPackName_throwIfNotCustom")
    void addCustomWordPackToWordDataByWordPackName_throwIfNotCustom(Platform platform, RoleName roleName) {
        // Given
        mockUser(1, roleName, platform);
        WordPack wordPackToBeAdded = new WordPack(WORD_PACK_NAME, DESCRIPTION, Category.HSK, platform);

        given(wordPackService.getByName(WORD_PACK_NAME)).willReturn(wordPackToBeAdded);
        doThrow(new BadRequestException("not custom"))
                .when(wordPackService).throwIfWordPackCategoryNotCustom(wordPackToBeAdded);

        // When / Then
        assertThatThrownBy(() -> underTest.addCustomWordPackToWordDataByWordPackName(WORD_PACK_NAME, WORD_PACK_NAME_ORIGINAL))
                .isInstanceOf(BadRequestException.class);
        verify(wordDataRepository, never()).saveAll(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#saveAll_persistsAll")
    void saveAll_persistsAll(Platform platform) {
        // Given
        List<WordData> input = List.of(buildWordData(WORD_DATA_ID, new ArrayList<>(), platform));

        // When
        underTest.saveAll(input);

        // Then
        verify(wordDataRepository).saveAll(input);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#saveAll_throwIfInvalidInput")
    void saveAll_throwIfInvalidInput(List<WordData> input) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.saveAll(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#deleteAll_deletesAll")
    void deleteAll_deletesAll(Platform platform) {
        // Given
        List<WordData> input = List.of(buildWordData(WORD_DATA_ID, new ArrayList<>(), platform));

        // When
        underTest.deleteAll(input);

        // Then
        verify(wordDataRepository).deleteAll(input);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#deleteAll_throwIfInvalidInput")
    void deleteAll_throwIfInvalidInput(List<WordData> input) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAll(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
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
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByWordPackNameAndPlatform_returnsIds")
    void getAllWordDataIdByWordPackNameAndPlatform_returnsIds(Platform platform) {
        // Given
        List<Integer> expected = List.of(1, 2);
        given(wordDataRepository.findAllWordDataIdsByWordPackNameAndPlatform(WORD_PACK_NAME_ORIGINAL, platform)).willReturn(expected);

        // When
        List<Integer> actual = underTest.getAllWordDataIdByWordPackNameAndPlatform(WORD_PACK_NAME_ORIGINAL, platform);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getAllWordDataIdByWordPackNameAndPlatform_throwIfInvalidInput")
    void getAllWordDataIdByWordPackNameAndPlatform_throwIfInvalidInput(String name, Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getAllWordDataIdByWordPackNameAndPlatform(name, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
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
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getIdByWordOfTheDayDateAndPlatform_throwIfInvalidInput")
    void getIdByWordOfTheDayDateAndPlatform_throwIfInvalidInput(Platform platform) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getIdByWordOfTheDayDateAndPlatform(platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void getEntityById_returnsEntity() {
        // Given
        WordData expected = buildWordData(WORD_DATA_ID, new ArrayList<>(), Platform.ENGLISH);
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.of(expected));

        // When
        WordData actual = underTest.getEntityById(WORD_DATA_ID);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getEntityById_throwIfNotFound() {
        // Given
        given(wordDataRepository.findById(WORD_DATA_ID)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getEntityById(WORD_DATA_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordDataServiceImplTest$TestDataSource#getEntityById_throwIfInvalidInput")
    void getEntityById_throwIfInvalidInput(Integer wordDataId) {
        // Given
        WordDataService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getEntityById(wordDataId))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
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
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        WordDataServiceImpl service = new WordDataServiceImpl(
                wordDataRepository,
                wordDataMapper,
                wordPackService,
                userService,
                roleService
        );
        return (WordDataService) processor.postProcessAfterInitialization(service, "wordDataService");
    }

    private static class TestDataSource {

        public static Stream<Arguments> getAllByPlatform_returnsAll() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllByPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getAllByWordPackNameAndPlatform_returnsAll() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getAllByWordPackNameAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments("", ENGLISH),
                    arguments(" ", ENGLISH),
                    arguments(null, CHINESE),
                    arguments("", CHINESE),
                    arguments(" ", CHINESE),
                    arguments("name", null)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_addsWhenMissing() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, "name"),
                    arguments(1, null),
                    arguments(1, ""),
                    arguments(1, " ")
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfAlreadyAdded() {
            return addCustomWordPackToWordData_addsWhenMissing();
        }

        public static Stream<Arguments> addCustomWordPackToWordData_throwIfNotCustom() {
            return addCustomWordPackToWordData_addsWhenMissing();
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_removesWhenPresent() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, "name"),
                    arguments(1, null),
                    arguments(1, ""),
                    arguments(1, " ")
            );
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfNotPresent() {
            return removeCustomWordPackFromWordData_removesWhenPresent();
        }

        public static Stream<Arguments> removeCustomWordPackFromWordData_throwIfNotCustom() {
            return removeCustomWordPackFromWordData_removesWhenPresent();
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackName_addsToAll() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackName_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, "orig"),
                    arguments("", "orig"),
                    arguments(" ", "orig"),
                    arguments("name", null),
                    arguments("name", ""),
                    arguments("name", " ")
            );
        }

        public static Stream<Arguments> addCustomWordPackToWordDataByWordPackName_throwIfNotCustom() {
            return addCustomWordPackToWordDataByWordPackName_addsToAll();
        }

        public static Stream<Arguments> saveAll_persistsAll() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> saveAll_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> deleteAll_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> deleteAll_deletesAll() {
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

        public static Stream<Arguments> getAllWordDataIdByWordPackNameAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments("", ENGLISH),
                    arguments(" ", ENGLISH),
                    arguments(null, CHINESE),
                    arguments("", CHINESE),
                    arguments(" ", CHINESE),
                    arguments("name", null)
            );
        }

        public static Stream<Arguments> getAllWordDataIdByWordPackNameAndPlatform_returnsIds() {
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

        public static Stream<Arguments> getEntityById_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }
    }
}
