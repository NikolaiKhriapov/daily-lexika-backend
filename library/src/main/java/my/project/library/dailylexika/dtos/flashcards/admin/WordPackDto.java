package my.project.library.dailylexika.dtos.flashcards.admin;

import my.project.library.dailylexika.enumerations.Category;
import my.project.library.dailylexika.enumerations.Platform;

public record WordPackDto(

        Long id,
        String name,
        String description,
        Category category,
        Platform platform,
        Integer userId
) {
}
