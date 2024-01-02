package my.project.models.mapper.flashcards;

import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.entity.flashcards.Review;
import my.project.models.entity.flashcards.Word;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    @Override
    public ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                entity.getWordPack().getName(),
                entity.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList()),
                entity.getDateLastCompleted(),
                entity.getDateGenerated()
        );
    }
}
