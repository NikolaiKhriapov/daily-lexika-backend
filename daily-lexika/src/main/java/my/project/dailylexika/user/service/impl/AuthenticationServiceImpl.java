package my.project.dailylexika.user.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.user.service.AuthenticationService;
import my.project.dailylexika.user.service.RoleService;
import my.project.dailylexika.user.service.UserService;
import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;
import my.project.library.dailylexika.events.user.AccountRegisteredEvent;
import my.project.library.util.security.JwtService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        boolean isUserAlreadyExists = userService.existsByEmail(registrationRequest.email());

        RoleName roleName = roleService.getRoleNameByPlatform(registrationRequest.platform());

        User user;
        if (!isUserAlreadyExists) {
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
        publishAccountRegisteredEvent(user, registrationRequest.platform(), isUserAlreadyExists);

        String jwtToken = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthenticationResponse(jwtToken);
    }

    @Override
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

    private void publishAccountRegisteredEvent(User user, Platform platform, boolean isUserAlreadyExists) {
        eventPublisher.publishEvent(
                AccountRegisteredEvent.builder()
                        .userId(user.getId())
                        .userEmail(user.getEmail())
                        .userName(user.getName())
                        .platform(platform)
                        .isUserAlreadyExists(isUserAlreadyExists)
                        .build()
        );
    }
}
