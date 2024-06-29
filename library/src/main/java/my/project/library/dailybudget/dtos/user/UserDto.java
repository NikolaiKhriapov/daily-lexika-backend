package my.project.library.dailybudget.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import my.project.library.dailybudget.enumerations.Language;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record UserDto(

        Integer id,

        @NotEmpty
        String email,

        Language interfaceLanguage,

        OffsetDateTime dateOfRegistration

) implements Serializable {
}
