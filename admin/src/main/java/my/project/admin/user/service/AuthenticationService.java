package my.project.admin.user.service;

import my.project.library.admin.dtos.user.AuthenticationRequest;
import my.project.library.admin.dtos.user.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
