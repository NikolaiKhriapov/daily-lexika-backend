package my.project.models.dto.chineseflashcards;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.models.entity.chineseflashcards.Category;
import my.project.models.entity.chineseflashcards.Review;

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
