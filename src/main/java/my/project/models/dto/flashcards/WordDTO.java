package my.project.models.dto.flashcards;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.models.entity.enumeration.Status;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record WordDTO(

        @Nullable
        Long id,

        WordDataDTO wordDataDTO,

        Status status,

        Integer currentStreak,

        Integer totalStreak,

        Integer occurrence,

        LocalDate dateOfLastOccurrence
) {
}
