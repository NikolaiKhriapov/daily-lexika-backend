package my.project.models.dto.flashcards;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.models.entity.enumeration.Category;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record WordPackDTO(

        String name,

        String description,

        Category category,

        @Nullable
        Long totalWords,

        @Nullable
        Long reviewId
) {
}
