package my.project.models.dto.flashcards;

import java.util.List;

public record StatisticsDTO(
        Long currentStreak,
        Long recordStreak,
        int wordsKnown,
//        int charactersKnown,
//        int idiomsKnown
        List<ReviewStatisticsDTO> listOfReviewStatisticsDTO
) {
}
