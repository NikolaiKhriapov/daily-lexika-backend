package my.project.models.dto.chineseflashcards;

public record ReviewStatisticsDTO(
        int wordsNew,
        int wordsInReview,
        int wordsKnown,
        int wordsTotal
) {
}