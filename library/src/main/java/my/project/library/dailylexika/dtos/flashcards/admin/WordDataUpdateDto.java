package my.project.library.dailylexika.dtos.flashcards.admin;

import java.util.List;
import java.util.Map;

public record WordDataUpdateDto(

        String nameChinese,
        String transcription,
        String nameEnglish,
        String nameRussian,
        String definition,
        List<Map<String, String>> examples,
        List<Long> listOfWordPackIds
) {
}
