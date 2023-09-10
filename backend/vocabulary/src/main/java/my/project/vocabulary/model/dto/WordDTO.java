package my.project.vocabulary.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.vocabulary.model.entity.Status;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
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

        LocalDate dateOfLastOccurrence,

        List<Long> listOfReviewId,

        List<Long> listOfChineseCharacterId,

        List<String> listOfWordPackNames
) {
}
