package my.project.vocabulary.model.dto;

import jakarta.annotation.Nullable;

import java.util.List;

public record ReviewDTO(

        @Nullable
        Long id,

        Integer maxNewWordsPerDay,

        Integer maxReviewWordsPerDay,

        String wordPackName,

        @Nullable
        List<Long> listOfWordId
) {
}
