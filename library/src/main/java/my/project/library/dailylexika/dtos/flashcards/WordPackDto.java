package my.project.library.dailylexika.dtos.flashcards;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;

import java.io.Serializable;

public record WordPackDto(

        @NotNull(message = "Field 'name' must not be null")
        @NotBlank(message = "Field 'name' must not be blank")
        String name,

        @NotNull(message = "Field 'description' must not be null")
        @NotBlank(message = "Field 'description' must not be blank")
        String description,

        Category category,

        Platform platform,

        @PositiveOrZero(message = "Field 'wordsTotal' must not be positive or zero")
        Long wordsTotal,

        @PositiveOrZero(message = "Field 'wordsNew' must not be positive or zero")
        Long wordsNew,

        @PositiveOrZero(message = "Field 'reviewId' must not be positive or zero")
        Long reviewId

) implements Serializable {
}
