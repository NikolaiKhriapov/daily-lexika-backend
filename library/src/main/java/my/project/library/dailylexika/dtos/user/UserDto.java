package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.RoleName;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

public record UserDto(

        Integer id,

        @NotEmpty
        String name,

        @NotEmpty
        String email,

        RoleName role,

        Set<RoleStatisticsDto> setOfRoleStatisticsDto,

        Language translationLanguage,

        Language interfaceLanguage,

        OffsetDateTime dateOfRegistration

) implements Serializable {
}
