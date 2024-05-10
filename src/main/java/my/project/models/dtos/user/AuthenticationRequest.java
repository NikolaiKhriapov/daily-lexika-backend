package my.project.models.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import my.project.models.entities.enumerations.Platform;

public record AuthenticationRequest(

        @NotNull(message = "Field 'email' must not be null")
        @NotBlank(message = "Field 'email' must not be blank")
        String email,

        @NotNull(message = "Field 'password' must not be null")
        @NotBlank(message = "Field 'password' must not be blank")
        String password,

        @NotNull(message = "Field 'platform' must not be null")
        Platform platform
) {
}
