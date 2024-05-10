package my.project.services.user;

import my.project.config.security.jwt.JwtService;
import my.project.models.dtos.user.AuthenticationRequest;
import my.project.models.dtos.user.AuthenticationResponse;
import my.project.models.dtos.user.RegistrationRequest;
import my.project.models.entities.enumerations.Platform;
import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.RoleStatistics;
import my.project.models.entities.user.User;
import my.project.services.flashcards.WordService;
import my.project.services.log.LogService;
import my.project.services.notification.NotificationService;
import my.project.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static my.project.util.data.TestDataUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class AuthenticationServiceTest extends AbstractUnitTest {

    private AuthenticationService underTest;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private WordService wordService;
    @Mock
    private UserService userService;
    @Mock
    private LogService logService;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationService(
                authenticationManager,
                jwtService,
                notificationService,
                passwordEncoder,
                roleService,
                wordService,
                userService,
                logService
        );
    }

//    @ParameterizedTest
//    @MethodSource("my.project.util.data.TestDataSource#register_newUser")
//    void register_newUser(Platform platform, RoleName newRoleName) {
//        // Given
//        RegistrationRequest registrationRequest = generateRegistrationRequest(platform);
//
//        given(userService.existsByEmail(any())).willReturn(false);
//        given(passwordEncoder.encode(any())).willReturn(ENCODED_PASSWORD);
//        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());
//        willCallRealMethod().given(roleService).addRoleToUserRoles(any(), any());
//
//        // When
//        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);
//
//        // Then
//        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
//        then(userService).should().save(userArgumentCaptor.capture());
//
//        User actualUser = userArgumentCaptor.getValue();
//        User expectedUser = User.builder()
//                .name(registrationRequest.name())
//                .email(registrationRequest.email().toLowerCase())
//                .password(ENCODED_PASSWORD)
//                .role(newRoleName)
//                .roleStatistics(Set.of(new RoleStatistics(newRoleName)))
//                .build();
//
//        assertThat(actualUser).isEqualTo(expectedUser);
//        then(notificationService).should().sendNotification(any());
//        assertThat(authenticationResponse).isNotNull();
//    }

    @ParameterizedTest
    @MethodSource("my.project.util.data.TestDataSource#register_existingUserNewPlatform")
    void register_existingUserNewPlatform(Platform platform, RoleName existingRoleName, RoleName newRoleName) {
        // Given
        RegistrationRequest registrationRequest = generateRegistrationRequest(platform);

        User existingUser = generateUser(registrationRequest, existingRoleName);

        given(userService.existsByEmail(any())).willReturn(true);
        given(userService.getUserByEmail(any())).willReturn(existingUser);
        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
        then(notificationService).shouldHaveNoInteractions();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        then(userService).should().save(userArgumentCaptor.capture());
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

        given(userService.getUserByEmail(any())).willReturn(existingUser);
        willCallRealMethod().given(roleService).getRoleNameByPlatform(any());
        willCallRealMethod().given(roleService).throwIfUserNotRegisteredOnPlatform(any(), any());

        // When
        AuthenticationResponse authenticationResponse = underTest.login(authenticationRequest);

        // Then
        assertThat(authenticationResponse).isNotNull();
    }

//    @ParameterizedTest
//    @EnumSource(Platform.class)
//    void login_throwIfUserNotRegisteredAtAll(Platform platform) {
//        // Given
//        AuthenticationRequest authenticationRequest = generateAuthenticationRequest(platform);
//
//        given(userService.getUserByEmail(any())).willReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> underTest.login(authenticationRequest)).isInstanceOf(UsernameNotFoundException.class);
//    }

}
