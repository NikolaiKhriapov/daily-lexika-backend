package my.project.models.dtos.user;

import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
public record UserDto(

        Long id,
        String name,
        String email,
        RoleName role,
        Set<RoleStatisticsDto> setOfRoleStatisticsDto

) implements Serializable {
}
