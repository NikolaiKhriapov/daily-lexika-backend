package my.project.library.dailylexika.dtos.flashcards;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public record ReviewDto(

        Long id,

        Long userId,

        @NotNull
        @PositiveOrZero
        Integer maxNewWordsPerDay,

        @NotNull
        @PositiveOrZero
        Integer maxReviewWordsPerDay,

        @NotNull
        WordPackDto wordPackDto,

        List<WordDto> listOfWordDto,

        Integer actualSize,

        OffsetDateTime dateLastCompleted,

        OffsetDateTime dateGenerated

) implements Serializable {
}
