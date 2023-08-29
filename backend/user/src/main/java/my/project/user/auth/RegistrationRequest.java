package my.project.user.auth;

import my.project.user.user.Gender;

public record RegistrationRequest(
        String name,
        String surname,
        String email,
        String password,
        Gender gender
) {
}
