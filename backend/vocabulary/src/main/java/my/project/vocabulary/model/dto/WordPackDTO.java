package my.project.vocabulary.model.dto;

import jakarta.annotation.Nullable;
import my.project.vocabulary.model.entity.Category;
import my.project.vocabulary.model.entity.Review;

import java.util.*;

public record WordPackDTO(

        String name,

        String description,

        Category category,

        @Nullable
        List<Long> listOfWordId,

        Review review
) {
}
