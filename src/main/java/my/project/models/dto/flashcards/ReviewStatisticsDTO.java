package my.project.models.dto.flashcards;

public record ReviewStatisticsDTO(
        Long wordPackId,
        String wordPackName,
        Integer wordsNew,
        Integer wordsInReview,
        Integer wordsKnown
) {
}