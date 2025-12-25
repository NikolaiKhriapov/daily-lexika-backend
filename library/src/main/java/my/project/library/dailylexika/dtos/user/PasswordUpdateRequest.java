package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(

        @NotBlank(message = "Field 'passwordCurrent' must not be blank")
        String passwordCurrent,

        @NotBlank(message = "Field 'passwordNew' must not be blank")
        String passwordNew
) {
}
