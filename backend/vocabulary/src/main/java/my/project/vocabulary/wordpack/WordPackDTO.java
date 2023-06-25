package my.project.vocabulary.wordpack;

import jakarta.annotation.Nullable;
import my.project.vocabulary.review.Review;

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
