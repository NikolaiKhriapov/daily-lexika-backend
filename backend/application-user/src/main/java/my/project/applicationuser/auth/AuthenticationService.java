package my.project.applicationuser.auth;

import lombok.RequiredArgsConstructor;
import my.project.amqp.RabbitMQMessageProducer;
import my.project.applicationuser.applicationuser.ApplicationUser;
import my.project.applicationuser.applicationuser.ApplicationUserRepository;
import my.project.applicationuser.applicationuser.ApplicationUserRole;
import my.project.applicationuser.jwt.JwtService;
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
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final FraudCheckClient fraudCheckClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public AuthenticationResponse register(RegistrationRequest registrationRequest) {

        // TODO: check if email not taken

        ApplicationUser applicationUser = ApplicationUser.builder()
                .name(registrationRequest.name())
                .surname(registrationRequest.surname())
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .gender(registrationRequest.gender())
                .applicationUserRole(ApplicationUserRole.USER)
                .build();

        applicationUserRepository.save(applicationUser);

        checkWhetherIsFraudster(applicationUser);

        sendNotification(applicationUser);

        String jwtToken = jwtService.generateToken(applicationUser);


        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );
        ApplicationUser applicationUser = (ApplicationUser) authentication.getPrincipal();

        String token = jwtService.generateToken(applicationUser);

        return new AuthenticationResponse(token);
    }

    private void checkWhetherIsFraudster(ApplicationUser applicationUser) {
        FraudCheckResponse fraudCheckResponse = fraudCheckClient.isFraudster(applicationUser.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }
    }

    private void sendNotification(ApplicationUser applicationUser) {
        NotificationRequest notificationRequest = new NotificationRequest(
                applicationUser.getId(),
                applicationUser.getEmail(),
                "Hi, %s, welcome to Chinese Learning App!".formatted(applicationUser.getName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
