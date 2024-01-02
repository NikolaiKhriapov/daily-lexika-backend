package my.project.models.dto.flashcards;

public record ReviewStatisticsDTO(
        int wordsNew,
        int wordsInReview,
        int wordsKnown,
        int wordsTotal
) {
}