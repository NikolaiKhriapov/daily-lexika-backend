package my.project.library.dailylexika.dtos.flashcards.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;

public record WordPackCreateDto(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        Category category,

        @NotNull
        Platform platform

)  {
}
