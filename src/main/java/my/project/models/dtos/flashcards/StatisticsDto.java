package my.project.models.dtos.flashcards;

import java.io.Serializable;
import java.util.List;

public record StatisticsDto(
        Long currentStreak,
        Long recordStreak,
        Integer wordsKnown,
        Integer charactersKnown,
//        Integer idiomsKnown
        List<ReviewStatisticsDto> listOfReviewStatisticsDto
) implements Serializable {
}
