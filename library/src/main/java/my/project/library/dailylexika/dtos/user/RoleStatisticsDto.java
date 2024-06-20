package my.project.library.dailylexika.dtos.user;

import my.project.library.dailylexika.enumerations.RoleName;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record RoleStatisticsDto(

        Long id,
        RoleName roleName,
        Long currentStreak,
        OffsetDateTime dateOfLastStreak,
        Long recordStreak

) implements Serializable {
}
