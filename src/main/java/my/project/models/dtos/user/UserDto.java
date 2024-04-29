package my.project.models.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import my.project.models.entities.enumeration.Language;
import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
public record UserDto(

        Long id,

        @NotEmpty
        String name,

        @NotEmpty
        String email,

        RoleName role,

        Set<RoleStatisticsDto> setOfRoleStatisticsDto,

        Language translationLanguage,

        Language interfaceLanguage

) implements Serializable {
}
