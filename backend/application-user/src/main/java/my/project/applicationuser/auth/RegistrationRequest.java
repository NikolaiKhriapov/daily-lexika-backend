package my.project.applicationuser.auth;

import my.project.applicationuser.applicationuser.Gender;

public record RegistrationRequest(
        String name,
        String surname,
        String email,
        String password,
        Gender gender
) {
}
