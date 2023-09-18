package my.project.vocabulary.model.dto;

public record ReviewStatisticsDTO(
        int wordsNew,
        int wordsInReview,
        int wordsKnown,
        int wordsTotal
) {
}