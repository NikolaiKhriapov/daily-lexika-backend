package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountDeletionRequest(

        @NotNull(message = "Field 'passwordCurrent' must not be null")
        @NotBlank(message = "Field 'passwordCurrent' must not be blank")
        String passwordCurrent
) {
}
