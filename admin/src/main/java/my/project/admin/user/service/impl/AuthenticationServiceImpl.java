package my.project.admin.user.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.admin.user.service.AuthenticationService;
import my.project.admin.user.service.UserService;
import my.project.library.admin.dtos.user.AuthenticationRequest;
import my.project.library.admin.dtos.user.AuthenticationResponse;
import my.project.admin.user.model.entities.User;
import my.project.library.util.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Transactional
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );
        User user = userService.getUserByEmail(authenticationRequest.email());

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new AuthenticationResponse(token);
    }
}
