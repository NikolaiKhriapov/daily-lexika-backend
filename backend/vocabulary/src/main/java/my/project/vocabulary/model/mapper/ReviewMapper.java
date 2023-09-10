package my.project.vocabulary.model.mapper;

import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.*;
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

    @Override
    public Review toEntity(ReviewDTO reviewDTO) {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    public ReviewDTO toDTOShort(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                null,
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                entity.getWordPack().getName(),
                null,
                entity.getDateLastCompleted(),
                null
        );
    }
}
