package my.project.library.dailylexika.dtos.user;

import jakarta.validation.constraints.NotBlank;
import my.project.library.dailylexika.enumerations.Language;
import my.project.library.dailylexika.enumerations.RoleName;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserDto(

        Integer id,

        @NotBlank
        String name,

        @NotBlank
        String email,

        RoleName role,

        Set<RoleStatisticsDto> setOfRoleStatisticsDto,

        Language translationLanguage,

        Language interfaceLanguage,

        OffsetDateTime dateOfRegistration

) {
}
