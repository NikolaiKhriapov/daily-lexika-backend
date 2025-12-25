package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record AccountDeletionRequest(

        @NotBlank(message = "Field 'passwordCurrent' must not be blank")
        String passwordCurrent
) {
}
