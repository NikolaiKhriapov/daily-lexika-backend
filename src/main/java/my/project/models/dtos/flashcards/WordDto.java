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
        Integer userId,
        WordDataDto wordDataDto,
        Status status,
        Short currentStreak,
        Short totalStreak,
        Short occurrence,
        LocalDate dateOfLastOccurrence

) implements Serializable {
}
