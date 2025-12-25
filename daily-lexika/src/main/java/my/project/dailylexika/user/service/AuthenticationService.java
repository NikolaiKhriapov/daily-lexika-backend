package my.project.dailylexika.user.service;

import my.project.library.dailylexika.dtos.user.AuthenticationRequest;
import my.project.library.dailylexika.dtos.user.AuthenticationResponse;
import my.project.library.dailylexika.dtos.user.RegistrationRequest;
import jakarta.validation.Valid;

public interface AuthenticationService {
    AuthenticationResponse register(@Valid RegistrationRequest registrationRequest);
    AuthenticationResponse login(@Valid AuthenticationRequest authenticationRequest);
}
