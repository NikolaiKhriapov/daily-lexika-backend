package my.project.dailylexika.user.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.user._public.PublicRoleService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.user.model.mappers.UserMapper;
import my.project.dailylexika.user.persistence.UserRepository;
import my.project.dailylexika.user.service.impl.UserServiceImpl;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.UserEmailUpdatedEvent;
import my.project.library.util.datetime.DateUtil;
import my.project.library.util.exception.BadRequestException;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static my.project.dailylexika.util.CommonConstants.ENCODED_PASSWORD;
import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class UserServiceImplTest extends AbstractUnitTest {

    private static final String NAME = "Test User";
    private static final String EMAIL = "User@Test.com";
    private static final String PASSWORD = "Pass123";
    private static final String NEW_PASSWORD = "Pass456";

    private UserServiceImpl underTest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PublicRoleService publicRoleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        underTest = new UserServiceImpl(
                userRepository,
                userMapper,
                roleService,
                publicRoleService,
                passwordEncoder,
                eventPublisher
        );
    }

    @Test
    void getPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        User userOne = buildUser(RoleName.USER_ENGLISH);
        User userTwo = buildUser(RoleName.USER_CHINESE);
        Page<User> pageOfUsers = new PageImpl<>(List.of(userOne, userTwo), pageable, 2);

        UserDto dtoOne = buildUserDto(userOne);
        UserDto dtoTwo = buildUserDto(userTwo);

        given(userRepository.findAll(pageable)).willReturn(pageOfUsers);
        given(userMapper.toDtoList(pageOfUsers.getContent())).willReturn(List.of(dtoOne, dtoTwo));

        // When
        Page<UserDto> actual = underTest.getPage(pageable);

        // Then
        assertThat(actual.getContent()).containsExactly(dtoOne, dtoTwo);
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getPage_empty() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> pageOfUsers = new PageImpl<>(List.of(), pageable, 0);

        given(userRepository.findAll(pageable)).willReturn(pageOfUsers);
        given(userMapper.toDtoList(pageOfUsers.getContent())).willReturn(List.of());

        // When
        Page<UserDto> actual = underTest.getPage(pageable);

        // Then
        assertThat(actual.getContent()).isEmpty();
        assertThat(actual.getTotalElements()).isZero();
    }

    @Test
    void save() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);

        // When
        underTest.save(user);

        // Then
        then(userRepository).should().save(user);
    }

    @Test
    void updateUserInfo() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);

        UserDto input = new UserDto(
                user.getId(),
                "Updated",
                user.getEmail(),
                user.getRole(),
                Set.of(),
                user.getTranslationLanguage(),
                user.getInterfaceLanguage(),
                user.getDateOfRegistration()
        );
        User updatedUser = buildUser(RoleName.USER_CHINESE);
        updatedUser.setName("Updated");
        updatedUser.setEmail(user.getEmail());
        UserDto expected = buildUserDto(updatedUser);

        given(userMapper.partialUpdate(input, user)).willReturn(updatedUser);
        given(userRepository.save(updatedUser)).willReturn(updatedUser);
        given(userMapper.toDto(updatedUser)).willReturn(expected);

        // When
        UserDto actual = underTest.updateUserInfo(input);

        // Then
        assertThat(actual).isEqualTo(expected);
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @Test
    void updateUserInfo_doNotPublishEventWhenEmailSameIgnoreCase() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);

        String inputEmail = "USER@TEST.COM";
        UserDto input = new UserDto(
                user.getId(),
                user.getName(),
                inputEmail,
                user.getRole(),
                Set.of(),
                user.getTranslationLanguage(),
                user.getInterfaceLanguage(),
                user.getDateOfRegistration()
        );
        User updatedUser = buildUser(RoleName.USER_CHINESE);
        updatedUser.setEmail(inputEmail);
        UserDto expected = buildUserDto(user);

        given(userMapper.partialUpdate(input, user)).willReturn(updatedUser);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userMapper.toDto(any(User.class))).willReturn(expected);

        // When
        UserDto actual = underTest.updateUserInfo(input);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(user.getEmail());
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateUserInfo_publishUserEmailUpdatedEvent")
    void updateUserInfo_publishUserEmailUpdatedEvent(RoleName roleName, Platform platform, String inputEmail, String expectedEmail) {
        // Given
        User user = buildUser(roleName);
        mockAuthentication(user);

        UserDto input = new UserDto(
                user.getId(),
                user.getName(),
                inputEmail,
                user.getRole(),
                Set.of(),
                user.getTranslationLanguage(),
                user.getInterfaceLanguage(),
                user.getDateOfRegistration()
        );
        User updatedUser = buildUser(roleName);
        updatedUser.setEmail(inputEmail);
        UserDto expected = new UserDto(
                updatedUser.getId(),
                updatedUser.getName(),
                expectedEmail,
                updatedUser.getRole(),
                Set.of(),
                updatedUser.getTranslationLanguage(),
                updatedUser.getInterfaceLanguage(),
                updatedUser.getDateOfRegistration()
        );
        RoleStatisticsDto roleStatisticsDto = new RoleStatisticsDto(1L, roleName, 0L, DateUtil.nowInUtc(), 0L);

        given(publicRoleService.getRoleStatistics()).willReturn(roleStatisticsDto);
        given(publicRoleService.getPlatformByRoleName(roleName)).willReturn(platform);
        given(userMapper.partialUpdate(input, user)).willReturn(updatedUser);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userMapper.toDto(any(User.class))).willReturn(expected);

        // When
        UserDto actual = underTest.updateUserInfo(input);

        // Then
        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(expectedEmail);
        ArgumentCaptor<UserEmailUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(UserEmailUpdatedEvent.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());
        UserEmailUpdatedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(user.getId());
        assertThat(event.platform()).isEqualTo(platform);
        assertThat(event.emailUpdated()).isEqualTo(expectedEmail);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateUserInfo_throwIfInvalidInput")
    void updateUserInfo_throwIfInvalidInput(UserDto userDto) {
        // Given
        UserService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updateUserInfo(userDto))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void updatePassword() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);
        PasswordUpdateRequest request = new PasswordUpdateRequest(PASSWORD, NEW_PASSWORD);

        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(NEW_PASSWORD)).willReturn("encoded-new");

        // When
        underTest.updatePassword(request);

        // Then
        assertThat(user.getPassword()).isEqualTo("encoded-new");
        then(userRepository).should().save(user);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updatePassword_throwIfInvalidInput")
    void updatePassword_throwIfInvalidInput(PasswordUpdateRequest request) {
        // Given
        UserService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updatePassword(request))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void updatePassword_throwIfPasswordIncorrect() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);
        PasswordUpdateRequest request = new PasswordUpdateRequest(PASSWORD, NEW_PASSWORD);

        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(false);

        // When / Then
        assertThatThrownBy(() -> underTest.updatePassword(request))
                .isInstanceOf(BadRequestException.class);
        then(userRepository).should(never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#deleteAccount")
    void deleteAccount(RoleName roleName, Platform platform) {
        // Given
        RoleStatistics currentRoleStatistics = new RoleStatistics(roleName);
        User user = buildUser(roleName, Set.of(currentRoleStatistics));
        mockAuthentication(user);
        AccountDeletionRequest request = new AccountDeletionRequest(PASSWORD);

        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(true);
        given(roleService.getRoleStatisticsEntity()).willReturn(currentRoleStatistics);
        given(publicRoleService.getPlatformByRoleName(roleName)).willReturn(platform);

        // When
        underTest.deleteAccount(request);

        // Then
        assertThat(user.getRoleStatistics()).isEmpty();
        then(userRepository).should().delete(user);
        ArgumentCaptor<AccountDeletedEvent> eventCaptor = ArgumentCaptor.forClass(AccountDeletedEvent.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());
        AccountDeletedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(user.getId());
        assertThat(event.userEmail()).isEqualTo(user.getEmail());
        assertThat(event.platform()).isEqualTo(platform);
        assertThat(event.isDeleteUser()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#deleteAccount_throwIfInvalidInput")
    void deleteAccount_throwIfInvalidInput(AccountDeletionRequest request) {
        // Given
        UserService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.deleteAccount(request))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#deleteAccount_removeRoleOnly")
    void deleteAccount_removeRoleOnly(RoleName roleName, RoleName remainingRole, Platform platform) {
        // Given
        RoleStatistics currentRoleStatistics = new RoleStatistics(roleName);
        RoleStatistics otherRoleStatistics = new RoleStatistics(remainingRole);
        User user = buildUser(roleName, Set.of(currentRoleStatistics, otherRoleStatistics));
        mockAuthentication(user);
        AccountDeletionRequest request = new AccountDeletionRequest(PASSWORD);

        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(true);
        given(roleService.getRoleStatisticsEntity()).willReturn(currentRoleStatistics);
        given(publicRoleService.getPlatformByRoleName(roleName)).willReturn(platform);

        // When
        underTest.deleteAccount(request);

        // Then
        assertThat(user.getRoleStatistics()).containsExactly(otherRoleStatistics);
        then(userRepository).should().save(user);
        ArgumentCaptor<AccountDeletedEvent> eventCaptor = ArgumentCaptor.forClass(AccountDeletedEvent.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());
        AccountDeletedEvent event = eventCaptor.getValue();
        assertThat(event.userId()).isEqualTo(user.getId());
        assertThat(event.userEmail()).isEqualTo(user.getEmail());
        assertThat(event.platform()).isEqualTo(platform);
        assertThat(event.isDeleteUser()).isFalse();
    }

    @Test
    void deleteAccount_throwIfPasswordIncorrect() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);
        AccountDeletionRequest request = new AccountDeletionRequest(PASSWORD);

        given(passwordEncoder.matches(PASSWORD, user.getPassword())).willReturn(false);

        // When / Then
        assertThatThrownBy(() -> underTest.deleteAccount(request))
                .isInstanceOf(BadRequestException.class);
        then(userRepository).should(never()).save(any());
        then(userRepository).should(never()).delete(any());
        then(eventPublisher).should(never()).publishEvent(any());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#existsByEmail")
    void existsByEmail(String inputEmail, String normalizedEmail, boolean expected) {
        // Given
        given(userRepository.existsByEmail(normalizedEmail)).willReturn(expected);

        // When
        boolean actual = underTest.existsByEmail(inputEmail);

        // Then
        assertThat(actual).isEqualTo(expected);
        then(userRepository).should().existsByEmail(normalizedEmail);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#existsByEmail_throwIfInvalidInput")
    void existsByEmail_throwIfInvalidInput(String inputEmail) {
        // Given
        UserService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.existsByEmail(inputEmail))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getUser() {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        mockAuthentication(user);
        UserDto expected = buildUserDto(user);
        given(userMapper.toDto(user)).willReturn(expected);

        // When
        UserDto actual = underTest.getUser();

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#getUserEntityByEmail")
    void getUserEntityByEmail(String inputEmail, String normalizedEmail) {
        // Given
        User user = buildUser(RoleName.USER_ENGLISH);
        given(userRepository.findUserByEmail(normalizedEmail)).willReturn(java.util.Optional.of(user));

        // When
        User actual = underTest.getUserEntityByEmail(inputEmail);

        // Then
        assertThat(actual).isEqualTo(user);
        then(userRepository).should().findUserByEmail(normalizedEmail);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#getUserEntityByEmail_throwIfInvalidInput")
    void getUserEntityByEmail_throwIfInvalidInput(String inputEmail) {
        // Given
        PublicUserService validatedService = createValidatedPublicService();

        // When / Then
        assertThatThrownBy(() -> validatedService.getUserEntityByEmail(inputEmail))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getUserEntityByEmail_throwIfUserNotFound() {
        // Given
        String normalizedEmail = EMAIL.toLowerCase();
        given(userRepository.findUserByEmail(normalizedEmail)).willReturn(java.util.Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getUserEntityByEmail(EMAIL))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateCurrentStreak")
    void updateCurrentStreak(Long newCurrentStreak) {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        RoleStatistics roleStatistics = new RoleStatistics(RoleName.USER_CHINESE);
        OffsetDateTime initialDate = DateUtil.nowInUtc().minusDays(5);
        roleStatistics.setDateOfLastStreak(initialDate);
        user.setRoleStatistics(Set.of(roleStatistics));
        mockAuthentication(user);

        given(roleService.getRoleStatisticsEntity()).willReturn(roleStatistics);

        // When
        underTest.updateCurrentStreak(newCurrentStreak);

        // Then
        assertThat(roleStatistics.getCurrentStreak()).isEqualTo(newCurrentStreak);
        assertThat(roleStatistics.getDateOfLastStreak()).isAfter(initialDate);
        then(userRepository).should().save(user);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateCurrentStreak_throwIfInvalidInput")
    void updateCurrentStreak_throwIfInvalidInput(Long newCurrentStreak) {
        // Given
        PublicUserService validatedService = createValidatedPublicService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updateCurrentStreak(newCurrentStreak))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateRecordStreak")
    void updateRecordStreak(Long newRecordStreak) {
        // Given
        User user = buildUser(RoleName.USER_CHINESE);
        RoleStatistics roleStatistics = new RoleStatistics(RoleName.USER_CHINESE);
        roleStatistics.setRecordStreak(3L);
        user.setRoleStatistics(Set.of(roleStatistics));
        mockAuthentication(user);

        given(roleService.getRoleStatisticsEntity()).willReturn(roleStatistics);

        // When
        underTest.updateRecordStreak(newRecordStreak);

        // Then
        assertThat(roleStatistics.getRecordStreak()).isEqualTo(newRecordStreak);
        then(userRepository).should().save(user);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.UserServiceImplTest$TestDataSource#updateRecordStreak_throwIfInvalidInput")
    void updateRecordStreak_throwIfInvalidInput(Long newRecordStreak) {
        // Given
        PublicUserService validatedService = createValidatedPublicService();

        // When / Then
        assertThatThrownBy(() -> validatedService.updateRecordStreak(newRecordStreak))
                .isInstanceOf(ConstraintViolationException.class);
    }

    private User buildUser(RoleName roleName) {
        return buildUser(roleName, new HashSet<>());
    }

    private User buildUser(RoleName roleName, Set<RoleStatistics> roleStatistics) {
        User user = User.builder()
                .id(101)
                .name(NAME)
                .email(EMAIL.toLowerCase())
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .dateOfRegistration(DateUtil.nowInUtc().minusDays(1))
                .build();
        user.setRoleStatistics(roleStatistics == null ? new HashSet<>() : new HashSet<>(roleStatistics));
        return user;
    }

    private UserDto buildUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                Set.of(),
                user.getTranslationLanguage(),
                user.getInterfaceLanguage(),
                user.getDateOfRegistration()
        );
    }

    private void mockAuthentication(User user) {
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(user);
    }

    private UserService createValidatedService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        UserServiceImpl service = new UserServiceImpl(
                userRepository,
                userMapper,
                roleService,
                publicRoleService,
                passwordEncoder,
                eventPublisher
        );
        return (UserService) processor.postProcessAfterInitialization(service, "userService");
    }

    private PublicUserService createValidatedPublicService() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        processor.afterPropertiesSet();
        UserServiceImpl service = new UserServiceImpl(
                userRepository,
                userMapper,
                roleService,
                publicRoleService,
                passwordEncoder,
                eventPublisher
        );
        return (PublicUserService) processor.postProcessAfterInitialization(service, "publicUserService");
    }

    private static class TestDataSource {

        public static Stream<Arguments> updateUserInfo_publishUserEmailUpdatedEvent() {
            return Stream.of(
                    arguments(USER_CHINESE, CHINESE, "Updated@Test.com", "updated@test.com"),
                    arguments(USER_ENGLISH, ENGLISH, "MIXED@Example.com", "mixed@example.com")
            );
        }

        public static Stream<Arguments> updateUserInfo_throwIfInvalidInput() {
            return Stream.of(
                    arguments(new UserDto(1, null, "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                    arguments(new UserDto(1, "", "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                    arguments(new UserDto(1, " ", "user@test.com", USER_ENGLISH, Set.of(), null, null, null)),
                    arguments(new UserDto(1, "User", null, USER_ENGLISH, Set.of(), null, null, null)),
                    arguments(new UserDto(1, "User", "", USER_ENGLISH, Set.of(), null, null, null)),
                    arguments(new UserDto(1, "User", " ", USER_ENGLISH, Set.of(), null, null, null))
            );
        }

        public static Stream<Arguments> updatePassword_throwIfInvalidInput() {
            return Stream.of(
                    arguments(new PasswordUpdateRequest(null, "newpass")),
                    arguments(new PasswordUpdateRequest("", "newpass")),
                    arguments(new PasswordUpdateRequest(" ", "newpass")),
                    arguments(new PasswordUpdateRequest("current", null)),
                    arguments(new PasswordUpdateRequest("current", "")),
                    arguments(new PasswordUpdateRequest("current", " "))
            );
        }

        public static Stream<Arguments> deleteAccount() {
            return Stream.of(
                    arguments(USER_CHINESE, CHINESE),
                    arguments(USER_ENGLISH, ENGLISH)
            );
        }

        public static Stream<Arguments> deleteAccount_removeRoleOnly() {
            return Stream.of(
                    arguments(USER_CHINESE, USER_ENGLISH, CHINESE),
                    arguments(USER_ENGLISH, USER_CHINESE, ENGLISH)
            );
        }

        public static Stream<Arguments> deleteAccount_throwIfInvalidInput() {
            return Stream.of(
                    arguments(new AccountDeletionRequest(null)),
                    arguments(new AccountDeletionRequest("")),
                    arguments(new AccountDeletionRequest(" "))
            );
        }

        public static Stream<Arguments> existsByEmail() {
            return Stream.of(
                    arguments("User@Test.com", "user@test.com", true),
                    arguments("lower@test.com", "lower@test.com", false)
            );
        }

        public static Stream<Arguments> existsByEmail_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(""),
                    arguments(" ")
            );
        }

        public static Stream<Arguments> getUserEntityByEmail() {
            return Stream.of(
                    arguments("User@Test.com", "user@test.com"),
                    arguments("MIXED@Example.com", "mixed@example.com")
            );
        }

        public static Stream<Arguments> getUserEntityByEmail_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(""),
                    arguments(" ")
            );
        }

        public static Stream<Arguments> updateCurrentStreak() {
            return Stream.of(
                    arguments(0L),
                    arguments(5L)
            );
        }

        public static Stream<Arguments> updateCurrentStreak_throwIfInvalidInput() {
            return Stream.of(
                    arguments((Object) null),
                    arguments(-1L),
                    arguments(-5L)
            );
        }

        public static Stream<Arguments> updateRecordStreak() {
            return updateCurrentStreak();
        }

        public static Stream<Arguments> updateRecordStreak_throwIfInvalidInput() {
            return updateCurrentStreak_throwIfInvalidInput();
        }
    }
}
