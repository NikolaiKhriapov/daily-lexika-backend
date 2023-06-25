package my.project.applicationuser.auth;

import lombok.RequiredArgsConstructor;
import my.project.applicationuser.dto.Response;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage(
                                "response.authentication.register", null, Locale.getDefault()))
                        .data(Map.of("authenticationResponse", authenticationService.register(registrationRequest)))
                        .build()
                );
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.authentication.login", null, Locale.getDefault()))
                        .data(Map.of("authenticationResponse", authenticationService.login(authenticationRequest)))
                        .build()
                );
    }
}
