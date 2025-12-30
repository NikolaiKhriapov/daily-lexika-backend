package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import my.project.library.dailylexika.enumerations.Platform;

import java.io.Serializable;

public record RegistrationRequest(

        @NotBlank(message = "Field 'name' must not be blank")
        String name,

        @NotBlank(message = "Field 'email' must not be blank")
        String email,

        @NotBlank(message = "Field 'password' must not be blank")
        String password,

        @NotNull(message = "Field 'platform' must not be null")
        Platform platform

) implements Serializable {
}
