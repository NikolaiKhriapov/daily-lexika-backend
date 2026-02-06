package my.project.library.dailylexika.dtos.flashcards;

import java.util.List;

public record StatisticsDto(

        Long currentStreak,
        Long recordStreak,
        Integer wordsKnown,
        Integer charactersKnown,
        List<ReviewStatisticsDto> listOfReviewStatisticsDto

) {
}
