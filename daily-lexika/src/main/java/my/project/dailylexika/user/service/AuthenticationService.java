package my.project.dailylexika.user.service;

import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegistrationRequest registrationRequest);
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
