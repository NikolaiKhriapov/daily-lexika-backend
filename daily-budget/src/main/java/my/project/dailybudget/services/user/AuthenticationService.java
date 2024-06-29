package my.project.dailybudget.services.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.dailybudget.entities.user.User;
import my.project.dailybudget.services.log.LogService;
import my.project.library.dailybudget.dtos.user.AuthenticationRequest;
import my.project.library.dailybudget.dtos.user.AuthenticationResponse;
import my.project.library.dailybudget.dtos.user.RegistrationRequest;
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
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final LogService logService;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        userService.throwIfUserAlreadyExists(registrationRequest.email());

        User user = new User(
                registrationRequest.email().toLowerCase(),
                passwordEncoder.encode(registrationRequest.password())
        );

        userService.save(user);
        logService.logAccountRegistration(user);

        String jwtToken = jwtService.generateToken(user.getUsername());
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

        String token = jwtService.generateToken(user.getUsername());
        return new AuthenticationResponse(token);
    }
}
