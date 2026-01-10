package my.project.library.dailylexika.dtos.flashcards;

public record ReviewStatisticsDto(

        Long reviewId,
        Long wordPackId,
        Integer wordsNew,
        Integer wordsInReview,
        Integer wordsKnown
) {
}
