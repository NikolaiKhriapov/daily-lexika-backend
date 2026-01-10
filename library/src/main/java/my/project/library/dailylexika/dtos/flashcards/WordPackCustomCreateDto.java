package my.project.library.dailylexika.dtos.flashcards;

import jakarta.validation.constraints.NotBlank;

public record WordPackCustomCreateDto(

        @NotBlank
        String name,

        @NotBlank
        String description
) {
}
