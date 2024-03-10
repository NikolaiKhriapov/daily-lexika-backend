package my.project.models.dtos.flashcards;

import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.flashcards.WordData;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link WordData}
 */
public record WordDataDto(

        Long id,
        String nameChineseSimplified,
        String transcription,
        String nameEnglish,
        String nameRussian,
        String definition,
        Set<String> examples,
        String audio,
        List<String> listOfWordPackNames,
        Platform platform

) implements Serializable {
}
