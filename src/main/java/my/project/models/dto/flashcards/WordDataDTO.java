package my.project.models.dto.flashcards;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.models.entity.enumeration.Platform;

import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record WordDataDTO(

        @Nullable
        Long id,

        String nameChineseSimplified,

        String transcription,

        String nameEnglish,

        String nameRussian,

        String definition,

        Set<String> examples,

        List<String> listOfWordPackNames,

        Platform platform
) {
}
