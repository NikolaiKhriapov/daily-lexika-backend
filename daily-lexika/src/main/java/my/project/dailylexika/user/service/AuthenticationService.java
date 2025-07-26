package my.project.dailylexika.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.notification.model.entities.Notification;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.dailylexika.log.service.LogService;
import my.project.dailylexika.notification.service.NotificationService;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.util.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final WordService wordService;
    private final UserService userService;
    private final LogService logService;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        boolean isEmailAlreadyExists = userService.existsByEmail(registrationRequest.email());

        RoleName roleName = roleService.getRoleNameByPlatform(registrationRequest.platform());

        User user;
        if (!isEmailAlreadyExists) {
            user = new User(
                    registrationRequest.name(),
                    registrationRequest.email().toLowerCase(),
                    passwordEncoder.encode(registrationRequest.password())
            );
        } else {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registrationRequest.email(),
                            registrationRequest.password()
                    )
            );
            user = userService.getUserByEmail(registrationRequest.email());
        }

        roleService.addRoleToUserRoles(user, roleName);
        user.setRole(roleName);

        userService.save(user);
        logService.logAccountRegistration(user, registrationRequest.platform());

        wordService.createAllWordsForUserAndPlatform(user.getId(), registrationRequest.platform());

        if (!isEmailAlreadyExists) {
            sendWelcomeNotificationToUser(user);
        }

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getRole().name());
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
        User user = userService.getUserByEmail(authenticationRequest.email());

        RoleName roleName = roleService.getRoleNameByPlatform(authenticationRequest.platform());

        roleService.throwIfUserNotRegisteredOnPlatform(user, roleName);

        user.setRole(roleName);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new AuthenticationResponse(token);
    }

    private void sendWelcomeNotificationToUser(User user) {
        notificationService.sendNotification(
                new Notification(
                        user.getId(),
                        user.getEmail(),
                        "Welcome to Daily Lexika!",
                        "Hello %s,\n\n".formatted(user.getName())
                                + "Congratulations on joining our vibrant community of language learners! We're thrilled to have you on board. Get ready for an exciting journey of daily vocabulary learning using the powerful spaced repetition approach.\n\n"
                                + "📚 <b>What to Expect:</b>\n"
                                + "<b>Personalized Learning</b>: Our app tailors the experience just for you, ensuring that your learning journey is effective and enjoyable.\n"
                                + "<b>Spaced Repetition Magic</b>: Say goodbye to cramming! Our spaced repetition technique will help you master new words and solidify your vocabulary in the most efficient way.\n\n"
                                + "🚀 <b>How to Get Started:</b>\n"
                                + "<b>Explore the Dashboard</b>: Take a tour of your personalized dashboard, where you'll find your daily reviews, word packs, and statistics.\n"
                                + "<b>Set Your Goals</b>: Define your language learning goals. Whether it's acing exams, improving communication, or just having fun, we're here to support you.\n"
                                + "<b>Daily Check-ins</b>: Make it a habit to check in daily. Consistency is key to language mastery.\n"
                                + "<b>Install App</b>: Once installed, you can access the app directly from your home screen like any other app, without needing to open your browser.\n"
                                + "<b>Create Custom Word Packs</b>: Take your learning to the next level by creating custom word packs tailored to your specific interests, needs, or learning objectives. Add words that you encounter in your daily life, textbooks, or conversations to personalize your learning experience further.\n\n"
                                + "Remember, the journey of a thousand words begins with a single step. We're here to make each step enjoyable and impactful.\n\n"
                                + "Happy learning!\n\n"
                                + "Best,\n"
                                + "The Daily Lexika Team"
                )
        );
    }
}
