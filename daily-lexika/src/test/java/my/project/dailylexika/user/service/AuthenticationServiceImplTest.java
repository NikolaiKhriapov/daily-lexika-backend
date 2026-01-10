package my.project.dailylexika.user.service;

import my.project.dailylexika.config.AbstractUnitTest;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.user.service.impl.AuthenticationServiceImpl;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.util.exception.ResourceAlreadyExistsException;
import my.project.library.util.exception.ResourceNotFoundException;
import my.project.library.util.security.JwtService;
import my.project.dailylexika.util.ValidationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.ConstraintViolationException;

import java.util.stream.Stream;

import static my.project.library.dailylexika.enumerations.Platform.CHINESE;
import static my.project.library.dailylexika.enumerations.Platform.ENGLISH;
import static my.project.library.dailylexika.enumerations.RoleName.USER_CHINESE;
import static my.project.library.dailylexika.enumerations.RoleName.USER_ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

class AuthenticationServiceImplTest extends AbstractUnitTest {

    private static final String TOKEN = "jwt-token";
    private static final String NAME = "Test User";
    private static final String EMAIL = "User@Test.com";
    private static final String PASSWORD = "Pass123";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    private AuthenticationServiceImpl underTest;
    @Mock
    private UserService userService;
    @Mock
    private PublicUserService publicUserService;
    @Mock
    private RoleService roleService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationServiceImpl(
                userService,
                publicUserService,
                roleService,
                jwtService,
                authenticationManager,
                passwordEncoder,
                eventPublisher
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_newUser")
    void register_newUser(Platform platform, RoleName roleName) {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(NAME, EMAIL, PASSWORD, platform);

        given(userService.existsByEmail(EMAIL.toLowerCase())).willReturn(false);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(roleName);
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        willDoNothing().given(roleService).addRoleToUserRoles(any(User.class), any(RoleName.class));
        given(jwtService.generateToken(anyString(), anyString())).willReturn(TOKEN);

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userService).should().save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(EMAIL.toLowerCase());
        then(jwtService).should().generateToken(savedUser.getUsername(), roleName.name());
        assertThat(authenticationResponse.token()).isEqualTo(TOKEN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_existingUserNewPlatform")
    void register_existingUserNewPlatform(Platform platform, RoleName existingRoleName, RoleName newRoleName) {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(NAME, EMAIL, PASSWORD, platform);
        User existingUser = buildUser(existingRoleName);

        given(userService.existsByEmail(EMAIL.toLowerCase())).willReturn(true);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(newRoleName);
        given(publicUserService.getUserEntityByEmail(EMAIL.toLowerCase())).willReturn(existingUser);
        willDoNothing().given(roleService).addRoleToUserRoles(any(User.class), any(RoleName.class));
        given(jwtService.generateToken(anyString(), anyString())).willReturn(TOKEN);

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        then(authenticationManager).should().authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo(EMAIL.toLowerCase());
        assertThat(authCaptor.getValue().getCredentials()).isEqualTo(PASSWORD);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userService).should().save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        then(jwtService).should().generateToken(savedUser.getUsername(), newRoleName.name());
        assertThat(authenticationResponse.token()).isEqualTo(TOKEN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_emailNormalization")
    void register_emailNormalization(Platform platform, RoleName roleName, String inputEmail, String expectedEmail) {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(NAME, inputEmail, PASSWORD, platform);

        given(userService.existsByEmail(expectedEmail)).willReturn(false);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(roleName);
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        willDoNothing().given(roleService).addRoleToUserRoles(any(User.class), any(RoleName.class));
        given(jwtService.generateToken(anyString(), anyString())).willReturn(TOKEN);

        // When
        underTest.register(registrationRequest);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userService).should().save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(expectedEmail);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_throwIfInvalidInput")
    void register_throwIfInvalidInput(RegistrationRequest registrationRequest) {
        // Given
        AuthenticationService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.register(registrationRequest))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_throwIfExistingUserInvalidPassword")
    void register_throwIfExistingUserInvalidPassword(Platform platform) {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(NAME, EMAIL, PASSWORD, platform);

        given(userService.existsByEmail(EMAIL.toLowerCase())).willReturn(true);
        willThrow(new BadCredentialsException("bad credentials"))
                .given(authenticationManager)
                .authenticate(any());

        // When / Then
        assertThatThrownBy(() -> underTest.register(registrationRequest))
                .isInstanceOf(BadCredentialsException.class);
        then(userService).should(never()).save(any());
        then(jwtService).should(never()).generateToken(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#register_throwIfExistingUserAlreadyHasRole")
    void register_throwIfExistingUserAlreadyHasRole(Platform platform, RoleName existingRoleName) {
        // Given
        RegistrationRequest registrationRequest = new RegistrationRequest(NAME, EMAIL, PASSWORD, platform);
        User existingUser = buildUser(existingRoleName);

        given(userService.existsByEmail(EMAIL.toLowerCase())).willReturn(true);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(existingRoleName);
        given(publicUserService.getUserEntityByEmail(EMAIL.toLowerCase())).willReturn(existingUser);
        willThrow(new ResourceAlreadyExistsException("already registered"))
                .given(roleService)
                .addRoleToUserRoles(existingUser, existingRoleName);

        // When / Then
        assertThatThrownBy(() -> underTest.register(registrationRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class);
        then(userService).should(never()).save(any());
        then(jwtService).should(never()).generateToken(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#login_success")
    void login_success(Platform platform, RoleName roleName) {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(EMAIL, PASSWORD, platform);
        User existingUser = buildUser(roleName);

        given(publicUserService.getUserEntityByEmail(EMAIL.toLowerCase())).willReturn(existingUser);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(roleName);
        willDoNothing().given(roleService).throwIfUserNotRegisteredOnPlatform(existingUser, roleName);
        given(jwtService.generateToken(anyString(), anyString())).willReturn(TOKEN);

        // When
        AuthenticationResponse authenticationResponse = underTest.login(authenticationRequest);

        // Then
        then(jwtService).should().generateToken(existingUser.getUsername(), roleName.name());
        assertThat(authenticationResponse.token()).isEqualTo(TOKEN);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#login_mixedCaseEmail")
    void login_mixedCaseEmail(Platform platform, RoleName roleName, String inputEmail) {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(inputEmail, PASSWORD, platform);
        String normalizedEmail = inputEmail.toLowerCase();
        User existingUser = User.builder()
                .id(101)
                .name(NAME)
                .email(normalizedEmail)
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .build();

        given(publicUserService.getUserEntityByEmail(normalizedEmail)).willReturn(existingUser);
        given(roleService.getRoleNameByPlatform(platform)).willReturn(roleName);
        willDoNothing().given(roleService).throwIfUserNotRegisteredOnPlatform(existingUser, roleName);
        given(jwtService.generateToken(anyString(), anyString())).willReturn(TOKEN);

        // When
        underTest.login(authenticationRequest);

        // Then
        then(publicUserService).should().getUserEntityByEmail(normalizedEmail);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#login_throwIfInvalidInput")
    void login_throwIfInvalidInput(AuthenticationRequest authenticationRequest) {
        // Given
        AuthenticationService validatedService = createValidatedService();

        // When / Then
        assertThatThrownBy(() -> validatedService.login(authenticationRequest))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#login_throwIfBadCredentials")
    void login_throwIfBadCredentials(Platform platform) {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(EMAIL, PASSWORD, platform);
        willThrow(new BadCredentialsException("bad credentials"))
                .given(authenticationManager)
                .authenticate(any());

        // When / Then
        assertThatThrownBy(() -> underTest.login(authenticationRequest))
                .isInstanceOf(BadCredentialsException.class);
        then(jwtService).should(never()).generateToken(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("my.project.dailylexika.user.service.AuthenticationServiceImplTest$TestDataSource#login_throwIfUserNotRegisteredOnPlatform")
    void login_throwIfUserNotRegisteredOnPlatform(Platform platform, RoleName roleName) {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(EMAIL, PASSWORD, platform);
        User existingUser = buildUser(roleName);

        given(publicUserService.getUserEntityByEmail(EMAIL.toLowerCase())).willReturn(existingUser);
        given(roleService.getRoleNameByPlatform(authenticationRequest.platform())).willReturn(roleName);
        willThrow(new ResourceNotFoundException("not registered"))
                .given(roleService)
                .throwIfUserNotRegisteredOnPlatform(existingUser, roleName);

        // When / Then
        assertThatThrownBy(() -> underTest.login(authenticationRequest))
                .isInstanceOf(ResourceNotFoundException.class);
        then(jwtService).should(never()).generateToken(anyString(), anyString());
    }

    private AuthenticationService createValidatedService() {
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                userService,
                publicUserService,
                roleService,
                jwtService,
                authenticationManager,
                passwordEncoder,
                eventPublisher
        );
        return ValidationTestSupport.validatedProxy(service, "authenticationService", AuthenticationService.class);
    }

    private User buildUser(RoleName roleName) {
        return User.builder()
                .id(101)
                .name(NAME)
                .email(EMAIL)
                .password(ENCODED_PASSWORD)
                .role(roleName)
                .build();
    }

    static class TestDataSource {

        public static Stream<Arguments> register_newUser() {
            return Stream.of(
                    arguments(CHINESE, USER_CHINESE),
                    arguments(ENGLISH, USER_ENGLISH)
            );
        }

        public static Stream<Arguments> register_existingUserNewPlatform() {
            return Stream.of(
                    arguments(CHINESE, USER_ENGLISH, USER_CHINESE),
                    arguments(ENGLISH, USER_CHINESE, USER_ENGLISH)
            );
        }

        public static Stream<Arguments> register_emailNormalization() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "User@Test.com", "user@test.com"),
                    arguments(CHINESE, USER_CHINESE, "UPPER@TEST.COM", "upper@test.com")
            );
        }

        public static Stream<Arguments> register_throwIfInvalidInput() {
            return Stream.of(
                    arguments(new RegistrationRequest(null, "user@test.com", "pass", ENGLISH)),
                    arguments(new RegistrationRequest(" ", "user@test.com", "pass", ENGLISH)),
                    arguments(new RegistrationRequest("", "user@test.com", "pass", ENGLISH)),
                    arguments(new RegistrationRequest("User", null, "pass", ENGLISH)),
                    arguments(new RegistrationRequest("User", " ", "pass", ENGLISH)),
                    arguments(new RegistrationRequest("User", "", "pass", ENGLISH)),
                    arguments(new RegistrationRequest("User", "user@test.com", null, ENGLISH)),
                    arguments(new RegistrationRequest("User", "user@test.com", " ", ENGLISH)),
                    arguments(new RegistrationRequest("User", "user@test.com", "", ENGLISH)),
                    arguments(new RegistrationRequest("User", "user@test.com", "pass", null))
            );
        }

        public static Stream<Arguments> register_throwIfExistingUserInvalidPassword() {
            return Stream.of(
                    arguments(CHINESE),
                    arguments(ENGLISH)
            );
        }

        public static Stream<Arguments> register_throwIfExistingUserAlreadyHasRole() {
            return register_newUser();
        }

        public static Stream<Arguments> login_success() {
            return Stream.of(
                    arguments(CHINESE, USER_CHINESE),
                    arguments(ENGLISH, USER_ENGLISH)
            );
        }

        public static Stream<Arguments> login_mixedCaseEmail() {
            return Stream.of(
                    arguments(ENGLISH, USER_ENGLISH, "User@Test.com"),
                    arguments(CHINESE, USER_CHINESE, "MiXeD@Example.com")
            );
        }

        public static Stream<Arguments> login_throwIfInvalidInput() {
            return Stream.of(
                    arguments(new AuthenticationRequest(null, "pass", ENGLISH)),
                    arguments(new AuthenticationRequest(" ", "pass", ENGLISH)),
                    arguments(new AuthenticationRequest("", "pass", ENGLISH)),
                    arguments(new AuthenticationRequest("user@test.com", null, ENGLISH)),
                    arguments(new AuthenticationRequest("user@test.com", " ", ENGLISH)),
                    arguments(new AuthenticationRequest("user@test.com", "", ENGLISH)),
                    arguments(new AuthenticationRequest("user@test.com", "pass", null))
            );
        }

        public static Stream<Arguments> login_throwIfBadCredentials() {
            return Stream.of(
                    arguments(CHINESE),
                    arguments(ENGLISH)
            );
        }

        public static Stream<Arguments> login_throwIfUserNotRegisteredOnPlatform() {
            return login_success();
        }
    }
}
