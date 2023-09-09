package my.project.vocabulary.model.dto;

import jakarta.annotation.Nullable;
import my.project.vocabulary.model.entity.Status;

import java.util.Date;
import java.util.List;

public record WordDTO(

        @Nullable
        Long id,

        String nameChineseSimplified,

        String nameChineseTraditional,

        String pinyin,

        String nameEnglish,

        String nameRussian,

        Status status,

        Integer currentStreak,

        Integer totalStreak,

        Integer occurrence,

        Date dateOfLastOccurrence,

        List<Long> listOfReviewId,

        List<Long> listOfChineseCharacterId,

        List<String> listOfWordPackNames
) {
}
