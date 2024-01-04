package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.entity.flashcards.Review;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    private final WordMapper wordMapper;

    @Override
    public ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                entity.getWordPack().getName(),
                entity.getListOfWords().stream()
                        .map(wordMapper::toDTOShort)
                        .collect(Collectors.toList()),
                entity.getDateLastCompleted(),
                entity.getDateGenerated()
        );
    }
}
