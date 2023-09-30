package my.project.chineseflashcards.model.dto;

public record ReviewStatisticsDTO(
        int wordsNew,
        int wordsInReview,
        int wordsKnown,
        int wordsTotal
) {
}