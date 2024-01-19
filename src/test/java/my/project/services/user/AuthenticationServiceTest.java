package my.project.services.user;

import my.project.config.security.jwt.JwtService;
import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.AuthenticationResponse;
import my.project.models.dto.user.RegistrationRequest;
import my.project.models.entity.enumeration.Platform;
import my.project.models.entity.user.RoleName;
import my.project.models.entity.user.RoleStatistics;
import my.project.models.entity.user.User;
import my.project.repositories.user.UserRepository;
import my.project.services.notification.NotificationService;
import my.project.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static my.project.util.CommonConstants.ENCODED_PASSWORD;
import static my.project.util.data.TestDataUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class AuthenticationServiceTest extends AbstractUnitTest {

    private AuthenticationService underTest;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationService(
                authenticationManager,
                jwtService,
                userRepository,
                notificationService,
                passwordEncoder,
                roleService,
                messageSource
        );
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#register_newUser")
    void register_newUser(Platform platform, RoleName newRoleName) {
        // Given
        RegistrationRequest registrationRequest = generateRegistrationRequest(platform);

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn(ENCODED_PASSWORD);
        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());
        willCallRealMethod().given(roleService).addRoleToUserRoles(any(), any());

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userArgumentCaptor.capture());

        User actualUser = userArgumentCaptor.getValue();
        User expectedUser = User.builder()
                .name(registrationRequest.name())
                .email(registrationRequest.email().toLowerCase())
                .password(ENCODED_PASSWORD)
                .role(newRoleName)
                .roleStatistics(Set.of(new RoleStatistics(newRoleName)))
                .build();

        assertThat(actualUser).isEqualTo(expectedUser);
        then(notificationService).should().sendNotification(any());
        assertThat(authenticationResponse).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#register_existingUserNewPlatform")
    void register_existingUserNewPlatform(Platform platform, RoleName existingRoleName, RoleName newRoleName) {
        // Given
        RegistrationRequest registrationRequest = generateRegistrationRequest(platform);

        User existingUser = generateUser(registrationRequest, existingRoleName);

        given(userRepository.existsByEmail(any())).willReturn(true);
        given(userRepository.findUserByEmail(any())).willReturn(Optional.of(existingUser));
        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
        then(notificationService).shouldHaveNoInteractions();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userArgumentCaptor.capture());
        User actualUser = userArgumentCaptor.getValue();

        existingUser.getRoleStatistics().add(new RoleStatistics(newRoleName));
        User expectedUser = User.builder()
                .id(existingUser.getId())
                .name(existingUser.getName())
                .email(existingUser.getEmail())
                .password(existingUser.getPassword())
                .role(newRoleName)
                .roleStatistics(existingUser.getRoleStatistics())
                .build();

        assertThat(actualUser).isEqualTo(expectedUser);
        assertThat(authenticationResponse).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#login")
    void login(Platform platform, RoleName roleName) {
        // Given
        AuthenticationRequest authenticationRequest = generateAuthenticationRequest(platform);

        User existingUser = generateUser(authenticationRequest, roleName);

        given(userRepository.findUserByEmail(any())).willReturn(Optional.of(existingUser));
        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());
        willCallRealMethod().given(roleService).throwIfUserNotRegisteredOnPlatform(any(), any());

        // When
        AuthenticationResponse authenticationResponse = underTest.login(authenticationRequest);

        // Then
        assertThat(authenticationResponse).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(Platform.class)
    void login_throwIfUserNotRegisteredAtAll(Platform platform) {
        // Given
        AuthenticationRequest authenticationRequest = generateAuthenticationRequest(platform);

        given(userRepository.findUserByEmail(any())).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.login(authenticationRequest)).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void getAuthenticatedUser() {
        // Given
        User mockUser = mock(User.class);
        mockAuthentication(mockUser);

        // When
        User actualUser = underTest.getAuthenticatedUser();

        // Then
        assertThat(actualUser).isEqualTo(mockUser);
    }
}
