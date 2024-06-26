package my.project.library.dailylexika.dtos.flashcards;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

public record ReviewDto(

        Long id,
        Long userId,
        Integer maxNewWordsPerDay,
        Integer maxReviewWordsPerDay,
        WordPackDto wordPackDto,
        List<WordDto> listOfWordDto,
        Integer actualSize,
        OffsetDateTime dateLastCompleted,
        OffsetDateTime dateGenerated

) implements Serializable {
}
