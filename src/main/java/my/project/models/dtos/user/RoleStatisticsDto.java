package my.project.models.dtos.user;

import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.RoleStatistics;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link RoleStatistics}
 */
public record RoleStatisticsDto(

        Long id,
        RoleName roleName,
        Long currentStreak,
        OffsetDateTime dateOfLastStreak,
        Long recordStreak

) implements Serializable {
}
