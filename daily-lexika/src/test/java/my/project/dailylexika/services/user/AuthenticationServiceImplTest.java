package my.project.dailylexika.services.user;

import my.project.dailylexika.user.service.RoleService;
import my.project.dailylexika.user.service.UserService;
import my.project.dailylexika.user.service.impl.AuthenticationServiceImpl;
import my.project.library.util.security.JwtService;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.config.AbstractUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static my.project.dailylexika.util.data.TestDataUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class AuthenticationServiceImplTest extends AbstractUnitTest {

    private AuthenticationServiceImpl underTest;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        underTest = new AuthenticationServiceImpl(
                userService,
                roleService,
                jwtService,
                authenticationManager,
                passwordEncoder,
                eventPublisher
        );
    }

//    @ParameterizedTest
//    @MethodSource("my.project.dailylexika.util.data.TestDataSource#register_newUser")
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
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#register_existingUserNewPlatform")
    void register_existingUserNewPlatform(Platform platform, RoleName existingRoleName, RoleName newRoleName) {
        // Given
        RegistrationRequest registrationRequest = generateRegistrationRequest(platform);
        User existingUser = generateUser(registrationRequest, existingRoleName);

        given(userService.existsByEmail(any())).willReturn(true);
        given(userService.getUserByEmail(any())).willReturn(existingUser);
        given(roleService.getRoleNameByPlatform(any())).willReturn(newRoleName);
        doAnswer(inv -> {
            User user      = inv.getArgument(0, User.class);
            RoleName role  = inv.getArgument(1, RoleName.class);
            user.setRole(role);
            user.getRoleStatistics().add(new RoleStatistics(role));
            return null;
        }).when(roleService).addRoleToUserRoles(any(), any());

        // When
        AuthenticationResponse authenticationResponse = underTest.register(registrationRequest);

        // Then
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
    @MethodSource("my.project.dailylexika.util.data.TestDataSource#login")
    void login(Platform platform, RoleName roleName) {
        // Given
        AuthenticationRequest authenticationRequest = generateAuthenticationRequest(platform);
        User existingUser = generateUser(authenticationRequest, roleName);

        given(userService.getUserByEmail(any())).willReturn(existingUser);
        given(roleService.getRoleNameByPlatform(any())).willReturn(roleName);
        willDoNothing().given(roleService).throwIfUserNotRegisteredOnPlatform(any(), any());

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
