package my.project.library.dailylexika.dtos.flashcards;

import java.io.Serializable;

public record ReviewStatisticsDto(

        Long reviewId,
        String wordPackName,
        Integer wordsNew,
        Integer wordsInReview,
        Integer wordsKnown

) implements Serializable {
}
