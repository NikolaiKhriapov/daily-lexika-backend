package my.project.library.dailylexika.dtos.flashcards;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;

public record WordPackUserDto(

        Long id,

        @NotBlank
        String name,

        @NotBlank
        String description,

        Category category,

        Platform platform,

        Integer userId,

        @PositiveOrZero
        Long wordsTotal,

        @PositiveOrZero
        Long wordsNew,

        @PositiveOrZero
        Long reviewId
) {
}
