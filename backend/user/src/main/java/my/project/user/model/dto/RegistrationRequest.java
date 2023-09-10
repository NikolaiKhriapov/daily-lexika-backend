package my.project.user.model.dto;

import my.project.user.model.entity.Gender;

public record RegistrationRequest(
        String name,
        String surname,
        String email,
        String password,
        Gender gender
) {
}
