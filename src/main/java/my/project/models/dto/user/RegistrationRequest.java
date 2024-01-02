package my.project.models.dto.user;

import my.project.models.entity.enumeration.Platform;

public record RegistrationRequest(
        String name,
        String email,
        String password,
        Platform platform
) {
}
