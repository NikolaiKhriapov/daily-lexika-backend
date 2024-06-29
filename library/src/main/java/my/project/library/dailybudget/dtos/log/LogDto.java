package my.project.library.dailybudget.dtos.log;

import my.project.library.dailylexika.enumerations.LogAction;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record LogDto(

    Long id,
    Integer userId,
    String userEmail,
    LogAction action,
    OffsetDateTime timestamp,
    String comment

) implements Serializable {
}
