package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceAlreadyExistsException;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.user.UserDTO;
import my.project.models.entity.notification.Notification;
import my.project.models.entity.user.Role;
import my.project.models.mapper.user.UserMapper;
import my.project.services.notification.NotificationService;
import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.AuthenticationResponse;
import my.project.models.dto.user.RegistrationRequest;
import my.project.models.entity.user.User;
import my.project.repositories.user.UserRepository;
import my.project.models.entity.user.RoleName;
import my.project.config.security.jwt.JwtService;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final MessageSource messageSource;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        boolean isEmailAlreadyExists = checkIfEmailAlreadyExists(registrationRequest.email());

        RoleName roleName = roleService.getRoleNameByPlatform(registrationRequest.platform());

        User user;
        if (!isEmailAlreadyExists) {
            user = User.builder()
                    .name(registrationRequest.name())
                    .email(registrationRequest.email().toLowerCase())
                    .password(passwordEncoder.encode(registrationRequest.password()))
                    .role(roleName)
                    .currentStreak(0L)
                    .dateOfLastStreak(LocalDate.now().minusDays(1))
                    .recordStreak(0L)
                    .build();
        } else {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registrationRequest.email(),
                            registrationRequest.password()
                    )
            );
            user = (User) authentication.getPrincipal();

            throwIfUserAlreadyHasRole(user, roleName);

            user.setRole(roleName);
        }

        roleService.addRoleToUserRoles(user, roleName);

        userRepository.save(user);

        if (!isEmailAlreadyExists) {
            sendWelcomeNotificationToUser(user);
        }

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );
        User user = (User) authentication.getPrincipal();

        RoleName roleName = roleService.getRoleNameByPlatform(authenticationRequest.platform());

        throwIfUserNotRegisteredOnPlatform(user, roleName);

        user.setRole(roleName);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public UserDTO getAuthenticatedUserDTO() {
        return userMapper.toDTO(getAuthenticatedUser());
    }

    private void sendWelcomeNotificationToUser(User user) {
        notificationService.sendNotification(
                new Notification(
                        user.getId(),
                        user.getEmail(),
                        "Welcome to Chinese Learning App!",
                        "Hi, %s, welcome to Chinese Learning App!".formatted(user.getName())
                )
        );
    }

    private boolean checkIfEmailAlreadyExists(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email.toLowerCase());
        return userOptional.isPresent();
    }

    private void throwIfUserAlreadyHasRole(User user, RoleName roleName) {
        List<RoleName> userRoleNames = user.getRoles().stream().map(Role::getRoleName).toList();
        if (userRoleNames.contains(roleName)) {
            throw new ResourceAlreadyExistsException(
                    messageSource.getMessage("exception.authentication.userAlreadyRegisteredOnPlatform", null, Locale.getDefault())
                            .formatted(user.getEmail(), roleService.getPlatformByRoleName(roleName))
            );
        }
    }

    private void throwIfUserNotRegisteredOnPlatform(User user, RoleName roleName) {
        List<RoleName> roleNames = user.getRoles().stream().map(Role::getRoleName).toList();
        if (!roleNames.contains(roleName)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("exception.authentication.userNotRegisteredOnPlatform", null, Locale.getDefault())
                            .formatted(user.getEmail(), roleService.getPlatformByRoleName(roleName))
            );
        }
    }
}
