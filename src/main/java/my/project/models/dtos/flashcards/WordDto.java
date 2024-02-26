package my.project.models.dtos.flashcards;

import my.project.models.entities.enumeration.Status;
import my.project.models.entities.flashcards.Word;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Word}
 */
public record WordDto(

        Long id,
        Long userId,
        WordDataDto wordDataDto,
        Status status,
        Integer currentStreak,
        Integer totalStreak,
        Integer occurrence,
        LocalDate dateOfLastOccurrence

) implements Serializable {
}
