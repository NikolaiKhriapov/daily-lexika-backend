package my.project.vocabulary.word;

import jakarta.annotation.Nullable;

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

        List<Long> listOfReviewId,

        List<Long> listOfChineseCharacterId,

        List<String> listOfWordPackNames
) {
}
