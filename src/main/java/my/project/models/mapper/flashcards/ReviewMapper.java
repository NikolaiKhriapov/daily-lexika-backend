package my.project.models.mapper.flashcards;

import lombok.AllArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.models.entity.flashcards.Review;
import my.project.models.mapper.Mapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    private final WordMapper wordMapper;
    private final WordPackMapper wordPackMapper;

    @Override
    public ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                entity.getUserId(),
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                wordPackMapper.toDTO(entity.getWordPack()),
                entity.getListOfWords().stream()
                        .map(wordMapper::toDTOShort)
                        .collect(Collectors.toList()),
                entity.getActualSize(),
                entity.getDateLastCompleted(),
                entity.getDateGenerated()
        );
    }
}
