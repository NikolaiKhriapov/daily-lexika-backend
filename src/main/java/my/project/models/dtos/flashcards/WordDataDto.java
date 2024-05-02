package my.project.models.dtos.flashcards;

import my.project.models.entities.enumeration.Platform;
import my.project.models.entities.flashcards.WordData;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
        List<String> examples,
        List<String> listOfWordPackNames,
        LocalDate wordOfTheDayDate,
        Platform platform

) implements Serializable {
}
