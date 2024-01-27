package my.project.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.models.entity.notification.Notification;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final MessageSource messageSource;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        boolean isEmailAlreadyExists = userRepository.existsByEmail(registrationRequest.email().toLowerCase());

        RoleName roleName = roleService.getRoleNameByPlatform(registrationRequest.platform());

        User user;
        if (!isEmailAlreadyExists) {
            user = User.builder()
                    .name(registrationRequest.name())
                    .email(registrationRequest.email().toLowerCase())
                    .password(passwordEncoder.encode(registrationRequest.password()))
                    .build();
        } else {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registrationRequest.email(),
                            registrationRequest.password()
                    )
            );
            user = getUserByEmail(registrationRequest.email());
        }

        roleService.addRoleToUserRoles(user, roleName);
        user.setRole(roleName);

        userRepository.save(user);

        if (!isEmailAlreadyExists) {
            sendWelcomeNotificationToUser(user);
        }

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );
        User user = getUserByEmail(authenticationRequest.email());

        RoleName roleName = roleService.getRoleNameByPlatform(authenticationRequest.platform());

        roleService.throwIfUserNotRegisteredOnPlatform(user, roleName);

        user.setRole(roleName);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(token);
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void sendWelcomeNotificationToUser(User user) {
        notificationService.sendNotification(
                new Notification(
                        user.getId(),
                        user.getEmail(),
                        "Welcome to Daily Lexika!",
                        "Hello %s,\n\n".formatted(user.getName())
                                + "Congratulations on joining our vibrant community of language learners! We're thrilled to have you on board. Get ready for an exciting journey of daily vocabulary learning using the powerful spaced repetition approach.\n\n"
                                + "📚 What to Expect:\n"
                                + "Personalized Learning: Our app tailors the experience just for you, ensuring that your learning journey is effective and enjoyable.\n"
                                + "Spaced Repetition Magic: Say goodbye to cramming! Our spaced repetition technique will help you master new words and solidify your vocabulary in the most efficient way.\n\n"
                                + "🚀 How to Get Started:\n"
                                + "Explore the Dashboard: Take a tour of your personalized dashboard, where you'll find your daily reviews, word packs, and statistics.\n"
                                + "Set Your Goals: Define your language learning goals. Whether it's acing exams, improving communication, or just having fun, we're here to support you.\n"
                                + "Daily Check-ins: Make it a habit to check in daily. Consistency is key to language mastery.\n\n"
                                + "Remember, the journey of a thousand words begins with a single step. We're here to make each step enjoyable and impactful.\n\n"
                                + "Happy learning!\n\n"
                                + "Best,\n"
                                + "The Daily Lexika Team"
                )
        );
    }

    private User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage(
                        "exception.authentication.usernameNotFound", null, Locale.getDefault())));
    }
}
