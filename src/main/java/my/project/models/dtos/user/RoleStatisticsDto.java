package my.project.models.dtos.user;

import my.project.models.entities.user.RoleName;
import my.project.models.entities.user.RoleStatistics;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link RoleStatistics}
 */
public record RoleStatisticsDto(

        Long id,
        RoleName roleName,
        Long currentStreak,
        LocalDate dateOfLastStreak,
        Long recordStreak

) implements Serializable {
}
