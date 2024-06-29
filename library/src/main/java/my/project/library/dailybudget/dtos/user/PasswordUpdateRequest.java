package my.project.library.dailybudget.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordUpdateRequest(

        @NotNull(message = "Field 'passwordCurrent' must not be null")
        @NotBlank(message = "Field 'passwordCurrent' must not be blank")
        String passwordCurrent,

        @NotNull(message = "Field 'passwordNew' must not be null")
        @NotBlank(message = "Field 'passwordNew' must not be blank")
        String passwordNew
) {
}
