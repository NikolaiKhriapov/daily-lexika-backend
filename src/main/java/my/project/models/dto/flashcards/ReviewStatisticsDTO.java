package my.project.models.dto.flashcards;

public record ReviewStatisticsDTO(
        Long wordPackId,
        String wordPackName,
        int wordsNew,
        int wordsInReview,
        int wordsKnown
) {
}