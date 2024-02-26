package my.project.models.dtos.flashcards;

import my.project.models.entities.flashcards.Review;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link Review}
 */
public record ReviewDto(

        Long id,
        Long userId,
        Integer maxNewWordsPerDay,
        Integer maxReviewWordsPerDay,
        WordPackDto wordPackDto,
        List<WordDto> listOfWordDto,
        Integer actualSize,
        LocalDate dateLastCompleted,
        LocalDate dateGenerated

) implements Serializable {
}