package my.project.vocabulary.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.vocabulary.model.entity.Category;
import my.project.vocabulary.model.entity.Review;

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
