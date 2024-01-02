package my.project.models.dto.user;

import my.project.models.entity.enumeration.Platform;

public record AuthenticationRequest(
        String email,
        String password,
        Platform platform
) {
}
