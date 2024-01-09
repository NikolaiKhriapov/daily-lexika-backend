package my.project.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import my.project.models.entity.user.RoleName;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record RoleStatisticsDTO(

        Long id,
        RoleName roleName,
        Long currentStreak,
        LocalDate dateOfLastStreak,
        Long recordStreak
) {
}
