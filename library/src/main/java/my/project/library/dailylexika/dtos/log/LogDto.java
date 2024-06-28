package my.project.library.dailylexika.dtos.log;

import my.project.library.dailylexika.enumerations.LogAction;
import my.project.library.dailylexika.enumerations.Platform;

import java.io.Serializable;
import java.time.OffsetDateTime;

public record LogDto(

    Long id,
    Integer userId,
    String userEmail,
    LogAction action,
    Platform platform,
    OffsetDateTime timestamp,
    String comment

) implements Serializable {
}
