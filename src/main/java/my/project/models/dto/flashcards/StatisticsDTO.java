package my.project.models.dto.flashcards;

import java.util.List;

public record StatisticsDTO(
        Long currentStreak,
        Long recordStreak,
        Integer wordsKnown,
        Integer charactersKnown,
//        Integer idiomsKnown
        List<ReviewStatisticsDTO> listOfReviewStatisticsDTO
) {
}
