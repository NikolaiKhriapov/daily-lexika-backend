package my.project.library.dailylexika.dtos.flashcards.admin;

import my.project.library.dailylexika.enumerations.Category;

public record WordPackUpdateDto(

        String description,
        Category category
) {
}
