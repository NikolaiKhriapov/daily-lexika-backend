package my.project.controllers.user;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.user.AuthenticationRequest;
import my.project.models.dto.user.AuthenticationResponse;
import my.project.services.user.AuthenticationService;
import my.project.models.dto.user.RegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }
}
