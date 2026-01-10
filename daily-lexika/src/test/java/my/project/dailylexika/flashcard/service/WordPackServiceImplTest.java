package my.project.dailylexika.flashcard.service;

import jakarta.validation.ConstraintViolationException;
import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.model.mappers.WordPackMapper;
import my.project.dailylexika.flashcard.persistence.WordPackRepository;
import my.project.dailylexika.flashcard.service.impl.WordPackServiceImpl;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.flashcards.WordPackCustomCreateDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackUserDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.events.flashcard.WordPackToBeDeletedEvent;
import my.project.library.util.exception.BadRequestException;
import my.project.library.util.exception.ResourceAlreadyExistsException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WordPackServiceImplTest extends AbstractUnitTest {

    private static final Integer USER_ID = 1;
    private static final String DESCRIPTION = "Description";
    private static final Long WORD_PACK_ID_1 = 1L;
    private static final Long WORD_PACK_ID_2 = 2L;
    private static final Long WORD_PACK_ID_3 = 3L;

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

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getAllForUser_returnsNonCustomAndOwnedCustom")
    void getAllForUser_returnsNonCustomAndOwnedCustom(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName, platform);
        List<WordPack> nonCustom = List.of(new WordPack(WORD_PACK_ID_1, "HSK_1", DESCRIPTION, Category.HSK, platform, null));
        List<WordPack> custom = List.of(new WordPack(WORD_PACK_ID_2, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID));
        List<WordPack> expectedPacks = new ArrayList<>();
        expectedPacks.addAll(nonCustom);
        expectedPacks.addAll(custom);
        List<WordPackUserDto> expectedDtos = List.of(new WordPackUserDto(WORD_PACK_ID_1, "dto", "dto", null, null, null, null, null, null));

        given(wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM)).willReturn(nonCustom);
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform)).willReturn(custom);
        given(wordPackMapper.toDtoList(expectedPacks)).willReturn(expectedDtos);

        // When
        List<WordPackUserDto> actual = underTest.getAllForUser();

        // Then
        assertThat(actual).isEqualTo(expectedDtos);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getAllForUser_filtersCustomByOwnerSuffix")
    void getAllForUser_filtersCustomByOwnerSuffix(Platform platform, RoleName roleName) {
        // Given
        UserDto user = mockUser(USER_ID, roleName, platform);
        List<WordPack> nonCustom = List.of(new WordPack(WORD_PACK_ID_1, "HSK_1", DESCRIPTION, Category.HSK, platform, null));
        List<WordPack> custom = List.of(
                new WordPack(WORD_PACK_ID_2, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID)
        );
        List<WordPack> expectedPacks = new ArrayList<>();
        expectedPacks.addAll(nonCustom);
        expectedPacks.add(new WordPack(WORD_PACK_ID_2, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID));
        List<WordPackUserDto> expectedDtos = List.of(new WordPackUserDto(WORD_PACK_ID_1, "dto", "dto", null, null, null, null, null, null));

        given(wordPackRepository.findAllByPlatformAndCategoryNot(platform, Category.CUSTOM)).willReturn(nonCustom);
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(user.id(), platform)).willReturn(custom);
        given(wordPackMapper.toDtoList(expectedPacks)).willReturn(expectedDtos);

        // When
        List<WordPackUserDto> actual = underTest.getAllForUser();

        // Then
        assertThat(actual).isEqualTo(expectedDtos);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getPage_returnsSortedNonCustom")
    void getPage_returnsSortedNonCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "A1", DESCRIPTION, Category.HSK, platform, null);
        given(wordPackRepository.findAllByPlatformAndCategoryNot(eq(platform), eq(Category.CUSTOM), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(wordPack)));

        // When
        Page<WordPackDto> result = underTest.getPage(platform, PageRequest.of(0, 10));

        // Then
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(wordPackRepository).findAllByPlatformAndCategoryNot(eq(platform), eq(Category.CUSTOM), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("name")).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo(wordPack.getName());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getById_returnsWordPack")
    void getById_returnsWordPack(Platform platform) {
        // Given
        WordPack expected = new WordPack(WORD_PACK_ID_1, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        given(wordPackRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // When
        WordPack actual = underTest.getById(expected.getId());

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getById_throwIfInvalidInput")
    void getById_throwIfInvalidInput(Long input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getById(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getById_throwIfNotFound() {
        // Given
        given(wordPackRepository.findById(WORD_PACK_ID_1)).willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getById(WORD_PACK_ID_1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getDtoById_returnsNonCustomDto")
    void getDtoById_returnsNonCustomDto(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "A1", DESCRIPTION, Category.HSK, platform, null);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When
        WordPackDto dto = underTest.getDtoById(wordPack.getId());

        // Then
        assertThat(dto.name()).isEqualTo(wordPack.getName());
        assertThat(dto.description()).isEqualTo(wordPack.getDescription());
        assertThat(dto.category()).isEqualTo(wordPack.getCategory());
        assertThat(dto.platform()).isEqualTo(wordPack.getPlatform());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getDtoById_throwIfCustom")
    void getDtoById_throwIfCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "Custom", DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When / Then
        assertThatThrownBy(() -> underTest.getDtoById(wordPack.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#getDtoById_throwIfInvalidInput")
    void getDtoById_throwIfInvalidInput(Long input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getDtoById(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly")
    void deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly(Platform platform) {
        // Given
        List<WordPack> custom = List.of(
                new WordPack(WORD_PACK_ID_1, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID),
                new WordPack(WORD_PACK_ID_2, "Other", DESCRIPTION, Category.CUSTOM, platform, USER_ID)
        );
        given(wordPackRepository.findAllByUserIdAndPlatformAndCategoryCustom(USER_ID, platform)).willReturn(custom);

        // When
        underTest.deleteAllByUserIdAndPlatform(USER_ID, platform);

        // Then
        verify(wordPackRepository).delete(custom.get(0));
        verify(wordPackRepository).delete(custom.get(1));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteAllByUserIdAndPlatform_throwIfInvalidInput")
    void deleteAllByUserIdAndPlatform_throwIfInvalidInput(Integer userId, Platform platform) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAllByUserIdAndPlatform(userId, platform))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#create_prefixesNonCustomName")
    void create_prefixesNonCustomName(Platform platform) {
        // Given
        WordPackCreateDto createDto = new WordPackCreateDto("Pack", DESCRIPTION, Category.HSK, platform);
        given(wordPackRepository.existsByPlatformAndName(platform, "Pack")).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        WordPackDto result = underTest.create(createDto);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Pack");
        assertThat(result.name()).isEqualTo("Pack");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#create_replacesWrongPrefix")
    void create_replacesWrongPrefix(Platform platform, String wrongPrefix) {
        // Given
        WordPackCreateDto createDto = new WordPackCreateDto(wrongPrefix + "Pack", DESCRIPTION, Category.HSK, platform);
        given(wordPackRepository.existsByPlatformAndName(platform, wrongPrefix + "Pack")).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        WordPackDto result = underTest.create(createDto);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(wrongPrefix + "Pack");
        assertThat(result.name()).isEqualTo(wrongPrefix + "Pack");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#create_throwIfInvalidInput")
    void create_throwIfInvalidInput(WordPackCreateDto input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.create(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#create_throwIfCategoryCustom")
    void create_throwIfCategoryCustom(Platform platform) {
        // Given
        WordPackCreateDto createDto = new WordPackCreateDto("Pack", DESCRIPTION, Category.CUSTOM, platform);

        // When / Then
        assertThatThrownBy(() -> underTest.create(createDto))
                .isInstanceOf(BadRequestException.class);
        verify(wordPackRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#create_throwIfAlreadyExists")
    void create_throwIfAlreadyExists(Platform platform) {
        // Given
        WordPackCreateDto createDto = new WordPackCreateDto("Pack", DESCRIPTION, Category.HSK, platform);
        given(wordPackRepository.existsByPlatformAndName(platform, "Pack")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.create(createDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
        verify(wordPackRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#update_updatesDescriptionAndCategory")
    void update_updatesDescriptionAndCategory(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "A1", DESCRIPTION, Category.HSK, platform, null);
        WordPackUpdateDto patchDto = new WordPackUpdateDto("New", Category.OTHER);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        WordPackDto result = underTest.update(wordPack.getId(), patchDto);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getDescription()).isEqualTo("New");
        assertThat(captor.getValue().getCategory()).isEqualTo(Category.OTHER);
        assertThat(result.category()).isEqualTo(Category.OTHER);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#update_throwIfInvalidInput")
    void update_throwIfInvalidInput(Long wordPackId, WordPackUpdateDto patchDto) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.update(wordPackId, patchDto))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#update_throwIfCategoryCustom")
    void update_throwIfCategoryCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "A1", DESCRIPTION, Category.HSK, platform, null);
        WordPackUpdateDto patchDto = new WordPackUpdateDto("New", Category.CUSTOM);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When / Then
        assertThatThrownBy(() -> underTest.update(wordPack.getId(), patchDto))
                .isInstanceOf(BadRequestException.class);
        verify(wordPackRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#delete_publishesEventAndDeletes")
    void delete_publishesEventAndDeletes(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "A1", DESCRIPTION, Category.HSK, platform, null);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When
        underTest.delete(wordPack.getId());

        // Then
        ArgumentCaptor<WordPackToBeDeletedEvent> eventCaptor = ArgumentCaptor.forClass(WordPackToBeDeletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        WordPackToBeDeletedEvent event = eventCaptor.getValue();
        assertThat(event.wordPackId()).isEqualTo(wordPack.getId());
        assertThat(event.platform()).isEqualTo(platform);
        verify(wordPackRepository).delete(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#delete_throwIfInvalidInput")
    void delete_throwIfInvalidInput(Long wordPackId) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.delete(wordPackId))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#delete_throwIfCustom")
    void delete_throwIfCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "Custom", DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When / Then
        assertThatThrownBy(() -> underTest.delete(wordPack.getId()))
                .isInstanceOf(BadRequestException.class);
        verify(wordPackRepository, never()).delete(any(WordPack.class));
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_createsWithDecoratedName")
    void createCustomWordPack_createsWithDecoratedName(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto input = new WordPackCustomCreateDto("MyPack", DESCRIPTION);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "MyPack", USER_ID)).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        WordPack saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("MyPack");
        assertThat(saved.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(saved.getCategory()).isEqualTo(Category.CUSTOM);
        assertThat(saved.getPlatform()).isEqualTo(platform);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_trimsNameBeforeDecoration")
    void createCustomWordPack_trimsNameBeforeDecoration(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto input = new WordPackCustomCreateDto("  Trim  ", DESCRIPTION);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "Trim", USER_ID)).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Trim");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_allowsCaseSensitiveDistinctNames")
    void createCustomWordPack_allowsCaseSensitiveDistinctNames(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto first = new WordPackCustomCreateDto("MyPack", DESCRIPTION);
        WordPackCustomCreateDto second = new WordPackCustomCreateDto("mypack", DESCRIPTION);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "MyPack", USER_ID)).willReturn(false);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "mypack", USER_ID)).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        underTest.createCustomWordPack(first);
        underTest.createCustomWordPack(second);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository, times(2)).save(captor.capture());
        List<WordPack> saved = captor.getAllValues();
        assertThat(saved).extracting(WordPack::getName)
                .containsExactlyInAnyOrder("MyPack", "mypack");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_preservesEmbeddedSuffix")
    void createCustomWordPack_preservesEmbeddedSuffix(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto input = new WordPackCustomCreateDto("Foo123", DESCRIPTION);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "Foo123", USER_ID)).willReturn(false);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Foo123");
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_throwIfInvalidInput")
    void createCustomWordPack_throwIfInvalidInput(WordPackCustomCreateDto input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.createCustomWordPack(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_acceptsNamesWithSemicolons")
    void createCustomWordPack_acceptsNamesWithSemicolons(Platform platform, RoleName roleName, String inputName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto input = new WordPackCustomCreateDto(inputName, DESCRIPTION);
        given(wordPackRepository.save(any(WordPack.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        underTest.createCustomWordPack(input);

        // Then
        ArgumentCaptor<WordPack> captor = ArgumentCaptor.forClass(WordPack.class);
        verify(wordPackRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(inputName.trim());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#createCustomWordPack_throwIfAlreadyExists")
    void createCustomWordPack_throwIfAlreadyExists(Platform platform, RoleName roleName) {
        // Given
        mockUser(USER_ID, roleName, platform);
        WordPackCustomCreateDto input = new WordPackCustomCreateDto("MyPack", DESCRIPTION);
        given(wordPackRepository.existsByPlatformAndNameAndUserId(platform, "MyPack", USER_ID)).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.createCustomWordPack(input))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_deletesAndPublishesEvent")
    void deleteCustomWordPack_deletesAndPublishesEvent(Platform platform, RoleName roleName) {
        // Given
        UserDto user = new UserDto(USER_ID, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "Pack", DESCRIPTION, Category.CUSTOM, platform, USER_ID);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When
        underTest.deleteCustomWordPack(wordPack.getId());

        // Then
        ArgumentCaptor<WordPackToBeDeletedEvent> eventCaptor = ArgumentCaptor.forClass(WordPackToBeDeletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        WordPackToBeDeletedEvent event = eventCaptor.getValue();
        assertThat(event.wordPackId()).isEqualTo(wordPack.getId());
        assertThat(event.platform()).isEqualTo(platform);
        verify(wordPackRepository).delete(wordPack);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_throwIfInvalidInput")
    void deleteCustomWordPack_throwIfInvalidInput(Long input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteCustomWordPack(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#deleteCustomWordPack_throwIfCategoryNotCustom")
    void deleteCustomWordPack_throwIfCategoryNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "HSK_1", DESCRIPTION, Category.HSK, platform, null);
        given(wordPackRepository.findById(wordPack.getId())).willReturn(Optional.of(wordPack));

        // When / Then
        assertThatThrownBy(() -> underTest.deleteCustomWordPack(wordPack.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#throwIfWordPackCategoryNotCustom_throwIfInvalidInput")
    void throwIfWordPackCategoryNotCustom_throwIfInvalidInput(WordPack input) {
        // Given
        WordPackService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.throwIfWordPackCategoryNotCustom(input))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.flashcard.service.WordPackServiceImplTest$TestDataSource#throwIfWordPackCategoryNotCustom_throwIfNotCustom")
    void throwIfWordPackCategoryNotCustom_throwIfNotCustom(Platform platform) {
        // Given
        WordPack wordPack = new WordPack(WORD_PACK_ID_1, "HSK_1", DESCRIPTION, Category.HSK, platform, null);

        // When / Then
        assertThatThrownBy(() -> underTest.throwIfWordPackCategoryNotCustom(wordPack))
                .isInstanceOf(BadRequestException.class);
    }

    private UserDto mockUser(Integer id, RoleName roleName, Platform platform) {
        UserDto user = new UserDto(id, "User", "user@test.com", roleName, Set.of(), null, null, null);
        given(userService.getUser()).willReturn(user);
        given(roleService.getPlatformByRoleName(roleName)).willReturn(platform);
        return user;
    }

    private WordPackService createValidatedService() {
        WordPackServiceImpl service = new WordPackServiceImpl(
                wordPackRepository,
                wordPackMapper,
                userService,
                roleService,
                eventPublisher
        );
        return ValidationTestSupport.validatedProxy(service, "wordPackService", WordPackService.class);
    }

    private static class TestDataSource {

        public static Stream<Arguments> getAllForUser_returnsNonCustomAndOwnedCustom() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getAllForUser_filtersCustomByOwnerSuffix() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> getPage_returnsSortedNonCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getById_returnsWordPack() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getById_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getDtoById_returnsNonCustomDto() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> getDtoById_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> getDtoById_throwIfCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_deletesOwnedCustomOnly() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> deleteAllByUserIdAndPlatform_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, ENGLISH),
                    arguments(1, null)
            );
        }

        public static Stream<Arguments> create_prefixesNonCustomName() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> create_replacesWrongPrefix() {
            return Stream.of(
                    arguments(ENGLISH, "WRONG_"),
                    arguments(CHINESE, "BAD_")
            );
        }

        public static Stream<Arguments> create_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(new WordPackCreateDto(null, DESCRIPTION, Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto("", DESCRIPTION, Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto(" ", DESCRIPTION, Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto("name", null, Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto("name", "", Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto("name", " ", Category.HSK, ENGLISH)),
                    arguments(new WordPackCreateDto("name", DESCRIPTION, null, ENGLISH)),
                    arguments(new WordPackCreateDto("name", DESCRIPTION, Category.HSK, null))
            );
        }

        public static Stream<Arguments> create_throwIfAlreadyExists() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> create_throwIfCategoryCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> update_updatesDescriptionAndCategory() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> update_throwIfInvalidInput() {
            return Stream.of(
                    arguments(null, new WordPackUpdateDto(DESCRIPTION, Category.HSK)),
                    arguments(WORD_PACK_ID_1, null)
            );
        }

        public static Stream<Arguments> update_throwIfCategoryCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> delete_publishesEventAndDeletes() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> delete_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> delete_throwIfCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> createCustomWordPack_createsWithDecoratedName() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createCustomWordPack_trimsNameBeforeDecoration() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createCustomWordPack_allowsCaseSensitiveDistinctNames() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createCustomWordPack_preservesEmbeddedSuffix() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> createCustomWordPack_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(new WordPackCustomCreateDto(null, "desc")),
                    arguments(new WordPackCustomCreateDto("", "desc")),
                    arguments(new WordPackCustomCreateDto(" ", "desc")),
                    arguments(new WordPackCustomCreateDto("name", null)),
                    arguments(new WordPackCustomCreateDto("name", "")),
                    arguments(new WordPackCustomCreateDto("name", " "))
            );
        }

        public static Stream<Arguments> createCustomWordPack_acceptsNamesWithSemicolons() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "  ;  "),
                    arguments(CHINESE, USER_CHINESE, "  ;  "),
                    arguments(ENGLISH, USER_ENGLISH, "name;with;semicolon"),
                    arguments(CHINESE, USER_CHINESE, "name;with;semicolon")
            );
        }

        public static Stream<Arguments> createCustomWordPack_throwIfAlreadyExists() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> deleteCustomWordPack_deletesAndPublishesEvent() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH),
                    arguments(CHINESE, USER_CHINESE)
            );
        }

        public static Stream<Arguments> deleteCustomWordPack_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> deleteCustomWordPack_throwIfCategoryNotCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }

        public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null)
            );
        }

        public static Stream<Arguments> throwIfWordPackCategoryNotCustom_throwIfNotCustom() {
            return Stream.of(
                    arguments(ENGLISH),
                    arguments(CHINESE)
            );
        }
    }
}
