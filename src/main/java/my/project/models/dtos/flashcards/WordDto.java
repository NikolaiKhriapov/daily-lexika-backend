package my.project.models.dtos.flashcards;

import my.project.models.entities.enumerations.Status;
import my.project.models.entities.flashcards.Word;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link Word}
 */
public record WordDto(

        Long id,
        Integer userId,
        WordDataDto wordDataDto,
        Status status,
        Short currentStreak,
        Short totalStreak,
        Short occurrence,
        OffsetDateTime dateOfLastOccurrence

) implements Serializable {
}
