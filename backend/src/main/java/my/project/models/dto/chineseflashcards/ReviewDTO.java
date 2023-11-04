package my.project.models.dto.chineseflashcards;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ReviewDTO(

        @Nullable
        Long id,

        Long userId,

        Integer maxNewWordsPerDay,

        Integer maxReviewWordsPerDay,

        String wordPackName,

        @Nullable
        List<Long> listOfWordId,

        LocalDate dateLastCompleted,

        LocalDate dateGenerated
) {
}