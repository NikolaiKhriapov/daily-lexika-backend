package my.project.chineseflashcards.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.chineseflashcards.model.entity.Category;
import my.project.chineseflashcards.model.entity.Review;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record WordPackDTO(

        String name,

        String description,

        Category category,

        @Nullable
        Long totalWords,

        Review review
) {
}
