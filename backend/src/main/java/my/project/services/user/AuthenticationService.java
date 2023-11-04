package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.exception.UserAlreadyExistsException;
import my.project.models.dto.user.UserDTO;
import my.project.models.entity.notification.Notification;
import my.project.models.mapper.user.UserMapper;
import my.project.services.fraudcheck.FraudCheckHistoryService;
import my.project.services.notification.NotificationService;
import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.AuthenticationResponse;
import my.project.models.dto.user.RegistrationRequest;
import my.project.models.entity.user.User;
import my.project.repositories.user.UserRepository;
import my.project.models.entity.user.UserRole;
import my.project.config.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final FraudCheckHistoryService fraudCheckHistoryService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {

        checkIfEmailTaken(registrationRequest.email());

        User user = User.builder()
                .name(registrationRequest.name())
                .email(registrationRequest.email().toLowerCase())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .userRole(UserRole.USER)
                .currentStreak(0L)
                .dateOfLastStreak(LocalDate.now().minusDays(1))
                .recordStreak(0L)
                .build();
        userRepository.save(user);

        checkWhetherIsFraudster(user);
        sendWelcomeNotificationToUser(user);

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

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public UserDTO getAuthenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    private void checkIfEmailTaken(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email.toLowerCase());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User '" + userOptional.get().getEmail() + "' already exists");
        }
    }

    private void checkWhetherIsFraudster(User user) {
        boolean isFraudster = fraudCheckHistoryService.isFraudster(user.getId());
        if (isFraudster) {
            throw new IllegalStateException("Fraudster");
        }
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
}
