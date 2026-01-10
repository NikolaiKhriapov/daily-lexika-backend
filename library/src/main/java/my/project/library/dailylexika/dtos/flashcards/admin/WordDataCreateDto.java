package my.project.library.dailylexika.dtos.flashcards.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;
import java.util.Map;

public record WordDataCreateDto(

        @NotBlank
        String nameChinese,

        @NotBlank
        String transcription,

        @NotBlank
        String nameEnglish,

        @NotBlank
        String nameRussian,

        @NotBlank
        String definition,

        @NotNull
        List<Map<String, String>> examples,

        @NotNull
        List<Long> listOfWordPackIds,

        @NotNull
        Platform platform
) {
}
