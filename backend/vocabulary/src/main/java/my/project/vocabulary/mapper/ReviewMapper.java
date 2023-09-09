package my.project.vocabulary.mapper;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.service.ReviewService;
import my.project.vocabulary.service.WordPackService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    private final WordPackService wordPackService;
    private final ReviewService reviewService;

    @Override
    public ReviewDTO toDTO(Review entity) {
        return new ReviewDTO(
                entity.getId(),
                entity.getMaxNewWordsPerDay(),
                entity.getMaxReviewWordsPerDay(),
                entity.getWordPack().getName(),
                entity.getListOfWords().stream()
                        .map(Word::getId)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Review toEntity(ReviewDTO reviewDTO) {
        WordPack wordPack = wordPackService.getWordPack(reviewDTO.wordPackName());
        List<Word> listOfWords = reviewService.generateListOfWordsForReview(wordPack, reviewDTO);

        return new Review(
                reviewDTO.maxNewWordsPerDay(),
                reviewDTO.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
        );
    }
}
