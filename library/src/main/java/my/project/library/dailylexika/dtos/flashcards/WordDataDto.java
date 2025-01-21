package my.project.library.dailylexika.dtos.flashcards;

import my.project.library.dailylexika.enumerations.Platform;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record WordDataDto(

        Long id,
        String nameChinese,
        String transcription,
        String nameEnglish,
        String nameRussian,
        String definition,
        List<Map<String, String>> examples,
        List<String> listOfWordPackNames,
        LocalDate wordOfTheDayDate,
        Platform platform

) implements Serializable {
}
