package my.project.vocabulary.review;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.word.Word;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewDTOMapper implements Function<Review, ReviewDTO> {

    @Override
    public ReviewDTO apply(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getMaxNewWordsPerDay(),
                review.getMaxReviewWordsPerDay(),
                review.getWordPack().getName(),
                review.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList())
        );
    }
}
