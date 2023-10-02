package my.project.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.amqp.RabbitMQMessageProducer;
import my.project.user.exception.UserAlreadyExistsException;
import my.project.user.model.dto.AuthenticationRequest;
import my.project.user.model.dto.AuthenticationResponse;
import my.project.user.model.dto.RegistrationRequest;
import my.project.user.model.entity.User;
import my.project.user.repository.UserRepository;
import my.project.user.model.entity.UserRole;
import my.project.user.security.jwt.JwtService;
import my.project.clients.fraudcheck.FraudCheckClient;
import my.project.clients.fraudcheck.FraudCheckResponse;
import my.project.clients.notification.NotificationRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final PasswordEncoder passwordEncoder;
    private final FraudCheckClient fraudCheckClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

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

    private void checkIfEmailTaken(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email.toLowerCase());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User '" + userOptional.get().getEmail() + "' already exists");
        }
    }

    private void checkWhetherIsFraudster(User user) {
        FraudCheckResponse fraudCheckResponse = fraudCheckClient.isFraudster(user.getId());
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }
    }

    private void sendWelcomeNotificationToUser(User user) {
        NotificationRequest notificationRequest = new NotificationRequest(
                user.getId(),
                user.getEmail(),
                "Welcome to Chinese Learning App!",
                "Hi, %s, welcome to Chinese Learning App!".formatted(user.getName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "notification.exchange",
                "notification.send-welcome-notification.routing-key"
        );
    }
}
