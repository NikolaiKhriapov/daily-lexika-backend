package my.project.library.dailylexika.dtos.flashcards;

import my.project.library.dailylexika.enumerations.Platform;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public record WordDataDto(

        Long id,
        String nameChinese,
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
