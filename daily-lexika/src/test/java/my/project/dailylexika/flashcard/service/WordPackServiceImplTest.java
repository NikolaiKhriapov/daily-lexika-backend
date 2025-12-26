package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.model.mappers.WordPackMapper;
import my.project.dailylexika.flashcard.persistence.WordPackRepository;
import my.project.dailylexika.flashcard.service.impl.WordPackServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.events.flashcard.CustomWordPackToBeDeletedEvent;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class WordPackServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;
    private static final String DESCRIPTION = "Description";

    private WordPackServiceImpl underTest;
    @Mock
    private WordPackRepository wordPackRepository;
    @Mock
    private WordPackMapper wordPackMapper;
    @Mock
    private PublicUserService userService;
    @Mock
    private PublicRoleService roleService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        underTest = new WordPackServiceImpl(
                wordPackRepository,
                wordPackMapper,
                userService,
                roleService,
                eventPublisher
        );
    }

    @Test
    void getAll_returnsAll() {
        // Given
        List<WordPack> expected = List.of(
                new WordPack("EN__Pack__1", DESCRIPTION, Category.CUSTOM, Platform.ENGLISH),
                new WordPack("HSK_1", DESCRIPTION, Category.HSK, Platform.CHINESE)
        );
        given(wordPackRepository.findAll()).willReturn(expected);

        // When
        List<WordPack> actual = underTest.getAll();

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getAllForUser_returnsNonCustomAndOwnedCustom")
    void getAllForUser_returnsNonCustomAndOwnedCustom(Platform platform, RoleName roleName, String prefix) {
        // Given
        UserDto user = mockUser(USER_ID, roleName, platform);
        List<WordPack> nonCustom = List.of(new WordPack("HSK_1", DESCRIPTION, Category.HSK, platform));
        List<WordPack> custom = List.of(new WordPack(prefix + "Pack__1", DESCRIPTION, Category.CUSTOM, platform));
        List<WordPack> expectedPacks = new ArrayList<>();
        expectedPacks.addAll(nonCustom);
        expectedPacks.addAll(custom);
        List<WordPackDto> expectedDtos = List.of(new WordPackDto("dto", "dto", null, null, null, null, null));

        given(wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM)).willReturn(nonCustom);
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform)).willReturn(custom);
        given(wordPackMapper.toDtoList(expectedPacks)).willReturn(expectedDtos);

        // When
        List<WordPackDto> actual = underTest.getAllForUser();

        // Then
        assertThat(actual).isEqualTo(expectedDtos);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getAllForUser_filtersCustomByOwnerSuffix")
    void getAllForUser_filtersCustomByOwnerSuffix(Platform platform, RoleName roleName, String prefix) {
        // Given
        UserDto user = mockUser(USER_ID, roleName, platform);
        List<WordPack> nonCustom = List.of(new WordPack("HSK_1", DESCRIPTION, Category.HSK, platform));
        List<WordPack> custom = List.of(
                new WordPack(prefix + "Pack__1", DESCRIPTION, Category.CUSTOM, platform),
                new WordPack(prefix + "Other__2", DESCRIPTION, Category.CUSTOM, platform)
        );
        List<WordPack> expectedPacks = new ArrayList<>();
        expectedPacks.addAll(nonCustom);
        expectedPacks.add(new WordPack(prefix + "Pack__1", DESCRIPTION, Category.CUSTOM, platform));
        List<WordPackDto> expectedDtos = List.of(new WordPackDto("dto", "dto", null, null, null, null, null));

        given(wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM)).willReturn(nonCustom);
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform)).willReturn(custom);
        given(wordPackMapper.toDtoList(expectedPacks)).willReturn(expectedDtos);

        // When
        List<WordPackDto> actual = underTest.getAllForUser();

        // Then
        assertThat(actual).isEqualTo(expectedDtos);
    }

    @Test
    void getByName_returnsWordPack() {
        // Given
        WordPack expected = new WordPack("EN__Pack__1", DESCRIPTION, Category.CUSTOM, Platform.ENGLISH);
        given(wordPackRepository.findById(expected.getName())).willReturn(Optional.of(expected));

        // When
        WordPack actual = underTest.getByName(expected.getName());

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getByName_throwIfInvalidInput")
    void getByName_throwIfInvalidInput(String input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getByName(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @Test
    void getByName_throwIfNotFound() {
        // Given
        given(wordPackRepository.findById("missing")).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getByName("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void saveAll_persistsAll() {
        // Given
        List<WordPack> input = List.of(
                new WordPack("EN__Pack__1", DESCRIPTION, Category.CUSTOM, Platform.ENGLISH),
                new WordPack("HSK_1", DESCRIPTION, Category.HSK, Platform.CHINESE)
        );

        // When
        underTest.saveAll(input);

        // Then
        verify(wordPackRepository).saveAll(input);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#saveAll_throwIfInvalidInput")
    void saveAll_throwIfInvalidInput(List<WordPack> input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.saveAll(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly")
    void deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly(Platform platform, String prefix) {
        // Given
        WordPackServiceImpl spy = Mockito.spy(underTest);
        doNothing().when(spy).deleteCustomWordPack(any());
        List<WordPack> custom = List.of(
                new WordPack(prefix + "Pack__1", DESCRIPTION, Category.CUSTOM, platform),
                new WordPack(prefix + "Other__1", DESCRIPTION, Category.CUSTOM, platform),
                new WordPack(prefix + "Foreign__2", DESCRIPTION, Category.CUSTOM, platform)
        );
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(USER_ID, platform)).willReturn(custom);

        // When
        spy.deleteAllByUserIdAndPlatform(USER_ID, platform);

        // Then
        verify(spy).deleteCustomWordPack(prefix + "Pack__1");
        verify(spy).deleteCustomWordPack(prefix + "Other__1");
        verify(spy, never()).deleteCustomWordPack(prefix + "Foreign__2");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_throwIfInvalidInput")
    void deleteAllByUserIdAndPlatform_throwIfInvalidInput(Integer userId, Platform platform) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAllByUserIdAndPlatform(userId, platform))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_createsWithDecoratedName")
    void createCustomWordPack_createsWithDecoratedName(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackDto input = new WordPackDto("MyPack", DESCRIPTION, null, null, null, null, null);
        given(wordPackRepository.existsById(prefix + "MyPack__1")).willReturn(false);

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        WordPack saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(prefix + "MyPack__1");
        assertThat(saved.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(saved.getCategory()).isEqualTo(Category.CUSTOM);
        assertThat(saved.getPlatform()).isEqualTo(platform);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_trimsNameBeforeDecoration")
    void createCustomWordPack_trimsNameBeforeDecoration(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackDto input = new WordPackDto("  Trim  ", DESCRIPTION, null, null, null, null, null);
        given(wordPackRepository.existsById(prefix + "Trim__1")).willReturn(false);

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(prefix + "Trim__1");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_allowsCaseSensitiveDistinctNames")
    void createCustomWordPack_allowsCaseSensitiveDistinctNames(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackDto first = new WordPackDto("MyPack", DESCRIPTION, null, null, null, null, null);
        WordPackDto second = new WordPackDto("mypack", DESCRIPTION, null, null, null, null, null);
        given(wordPackRepository.existsById(prefix + "MyPack__1")).willReturn(false);
        given(wordPackRepository.existsById(prefix + "mypack__1")).willReturn(false);

        // When
        underTest.createCustomWordPack(first);
        underTest.createCustomWordPack(second);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository, Mockito.times(2)).save(captor.capture());
        List<WordPack> saved = captor.getAllValues();
        assertThat(saved).extracting(WordPack::getName)
                .containsExactlyInAnyOrder(prefix + "MyPack__1", prefix + "mypack__1");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_preservesEmbeddedSuffix")
    void createCustomWordPack_preservesEmbeddedSuffix(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackDto input = new WordPackDto("Foo__123", DESCRIPTION, null, null, null, null, null);
        given(wordPackRepository.existsById(prefix + "Foo__123__1")).willReturn(false);

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(prefix + "Foo__123__1");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_throwIfInvalidInput")
    void createCustomWordPack_throwIfInvalidInput(WordPackDto input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.createCustomWordPack(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_throwIfInvalidName")
    void createCustomWordPack_throwIfInvalidName(String inputName) {
        // Given
        mockUser(USER_ID, RoleName.USER_ENGLISH, Platform.ENGLISH);
        WordPackDto input = new WordPackDto(inputName, DESCRIPTION, null, null, null, null, null);

        // When / Then
        assertThatThrownBy(() -> underTest.createCustomWordPack(input))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_throwIfAlreadyExists")
    void createCustomWordPack_throwIfAlreadyExists(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackDto input = new WordPackDto("MyPack", DESCRIPTION, null, null, null, null, null);
        given(wordPackRepository.existsById(prefix + "MyPack__1")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.createCustomWordPack(input))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_deletesAndPublishesEvent")
    void deleteCustomWordPack_deletesAndPublishesEvent(Platform platform, RoleName roleName, String prefix) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPack wordPack = new WordPack(prefix + "Pack__1", DESCRIPTION, Category.CUSTOM, platform);
        given(wordPackRepository.findById(wordPack.getName())).willReturn(Optional.of(wordPack));

        // When
        underTest.deleteCustomWordPack(wordPack.getName());

        // Then
        ArgumentCaptor<CustomWordPackToBeDeletedEvent> eventCaptor = ArgumentCaptor.forClass(CustomWordPackToBeDeletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        CustomWordPackToBeDeletedEvent event = eventCaptor.getValue();
        assertThat(event.wordPackName()).isEqualTo(wordPack.getName());
        assertThat(event.platform()).isEqualTo(platform);
        verify(wordPackRepository).delete(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_throwIfInvalidInput")
    void deleteCustomWordPack_throwIfInvalidInput(String input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteCustomWordPack(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_throwIfCategoryNotCustom")
    void deleteCustomWordPack_throwIfCategoryNotCustom(Platform platform, RoleName roleName, String prefix) {
        // Given
        WordPack wordPack = new WordPack(prefix + "HSK_1", DESCRIPTION, Category.HSK, platform);
        given(wordPackRepository.findById(wordPack.getName())).willReturn(Optional.of(wordPack));

        // When / Then
        assertThatThrownBy(() -> underTest.deleteCustomWordPack(wordPack.getName()))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#throwIfWordPackCategoryNotCustom_throwIfNotCustom")
    void throwIfWordPackCategoryNotCustom_throwIfNotCustom(Platform platform, RoleName roleName, String prefix) {
        // Given
        WordPack wordPack = new WordPack(prefix + "HSK_1", DESCRIPTION, Category.HSK, platform);

        // When / Then
        assertThatThrownBy(() -> underTest.throwIfWordPackCategoryNotCustom(wordPack))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#throwIfWordPackCategoryNotCustom_throwIfInvalidInput")
    void throwIfWordPackCategoryNotCustom_throwIfInvalidInput(WordPack input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.throwIfWordPackCategoryNotCustom(input))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }

    private UserDto mockUser(Integer id, RoleName roleName, Platform platform) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        return user;
    }

    private WordPackService createValidatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        WordPackServiceImpl service = new WordPackServiceImpl(
                wordPackRepository,
                wordPackMapper,
                userService,
                roleService,
                eventPublisher
        );
        return (WordPackService) processor.postProcessAfterInitialization(service, "wordPackService");
    }

    private static class TestDataSource {

        public static Stream<Arguments> getAllForUser_returnsNonCustomAndOwnedCustom() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "EN__"),
                    arguments(CHINESE, USER_CHINESE, "CH__")
            );
        }

        public static Stream<Arguments> getAllForUser_filtersCustomByOwnerSuffix() {
            return getAllForUser_returnsNonCustomAndOwnedCustom();
        }

        public static Stream<Arguments> getByName_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(""),
                    arguments(" ")
            );
        }

        public static Stream<Arguments> saveAll_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly() {
            return Stream.of(
                    arguments(ENGLISH, "EN__"),
                    arguments(CHINESE, "CH__")
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(1, null)
            );
        }

        public static Stream<Arguments> createCustomWordPack_createsWithDecoratedName() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "EN__"),
                    arguments(CHINESE, USER_CHINESE, "CH__")
            );
        }

        public static Stream<Arguments> createCustomWordPack_trimsNameBeforeDecoration() {
            return createCustomWordPack_createsWithDecoratedName();
        }

        public static Stream<Arguments> createCustomWordPack_allowsCaseSensitiveDistinctNames() {
            return createCustomWordPack_createsWithDecoratedName();
        }

        public static Stream<Arguments> createCustomWordPack_preservesEmbeddedSuffix() {
            return createCustomWordPack_createsWithDecoratedName();
        }

        public static Stream<Arguments> createCustomWordPack_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(new WordPackDto(null, "desc", null, null, null, null, null)),
                    arguments(new WordPackDto("", "desc", null, null, null, null, null)),
                    arguments(new WordPackDto(" ", "desc", null, null, null, null, null)),
                    arguments(new WordPackDto("name", null, null, null, null, null, null)),
                    arguments(new WordPackDto("name", "", null, null, null, null, null)),
                    arguments(new WordPackDto("name", " ", null, null, null, null, null))
            );
        }

        public static Stream<Arguments> createCustomWordPack_throwIfInvalidName() {
            return Stream.of(
                    arguments("  ;  "),
                    arguments("name;with;semicolon")
            );
        }

        public static Stream<Arguments> createCustomWordPack_throwIfAlreadyExists() {
            return createCustomWordPack_createsWithDecoratedName();
        }

        public static Stream<Arguments> deleteCustomWordPack_deletesAndPublishesEvent() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "EN__"),
                    arguments(CHINESE, USER_CHINESE, "CH__")
            );
        }

        public static Stream<Arguments> deleteCustomWordPack_throwIfInvalidInput() {
            return getByName_throwIfInvalidInput();
        }

        public static Stream<Arguments> deleteCustomWordPack_throwIfCategoryNotCustom() {
            return deleteCustomWordPack_deletesAndPublishesEvent();
        }

        public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfNotCustom() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "EN__"),
                    arguments(CHINESE, USER_CHINESE, "CH__")
            );
        }

        public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }
    }
}
