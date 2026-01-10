package my.project.library.dailylexika.dtos.flashcards;

import my.project.library.dailylexika.enumerations.Platform;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record WordDataDto(

        Integer id,
        String nameChinese,
        String transcription,
        String nameEnglish,
        String nameRussian,
        String definition,
        List<Map<String, String>> examples,
        List<Long> listOfWordPackIds,
        LocalDate wordOfTheDayDate,
        Platform platform
) {
}
