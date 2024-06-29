package my.project.library.dailybudget.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record RegistrationRequest(

        @NotNull(message = "Field 'email' must not be null")
        @NotBlank(message = "Field 'email' must not be blank")
        String email,

        @NotNull(message = "Field 'password' must not be null")
        @NotBlank(message = "Field 'password' must not be blank")
        String password
) implements Serializable {
}
