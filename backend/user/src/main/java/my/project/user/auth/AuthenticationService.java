package my.project.user.auth;

import lombok.RequiredArgsConstructor;
import my.project.amqp.RabbitMQMessageProducer;
import my.project.user.user.User;
import my.project.user.user.UserRepository;
import my.project.user.user.UserRole;
import my.project.user.jwt.JwtService;
import my.project.clients.fraudcheck.FraudCheckClient;
import my.project.clients.fraudcheck.FraudCheckResponse;
import my.project.clients.notification.NotificationRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FraudCheckClient fraudCheckClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public AuthenticationResponse register(RegistrationRequest registrationRequest) {

        // TODO: check if email not taken

        User user = User.builder()
                .name(registrationRequest.name())
                .surname(registrationRequest.surname())
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .gender(registrationRequest.gender())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);

        checkWhetherIsFraudster(user);

        sendNotification(user);

        String jwtToken = jwtService.generateToken(user);


        return new AuthenticationResponse(jwtToken);
    }

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

    private void checkWhetherIsFraudster(User user) {
        FraudCheckResponse fraudCheckResponse = fraudCheckClient.isFraudster(user.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }
    }

    private void sendNotification(User user) {
        NotificationRequest notificationRequest = new NotificationRequest(
                user.getId(),
                user.getEmail(),
                "Hi, %s, welcome to Chinese Learning App!".formatted(user.getName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
