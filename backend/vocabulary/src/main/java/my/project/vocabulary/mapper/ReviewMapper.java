package my.project.vocabulary.mapper;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.entity.Review;
import my.project.vocabulary.model.entity.Status;
import my.project.vocabulary.model.entity.Word;
import my.project.vocabulary.model.entity.WordPack;
import my.project.vocabulary.service.WordPackService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewMapper implements Mapper<Review, ReviewDTO> {

    private final WordPackService wordPackService;

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
    public Review toEntity(ReviewDTO dto) {
        WordPack wordPack = wordPackService.getWordPack(dto.wordPackName());

        Set<Word> newWords = new HashSet<>(wordPack.getListOfWords()).stream()
                .filter(word -> word.getStatus().equals(Status.NEW))
                .limit(dto.maxNewWordsPerDay())
                .collect(Collectors.toSet());
        Set<Word> reviewWords = new HashSet<>(wordPack.getListOfWords()).stream()
                .filter(word -> word.getStatus().equals(Status.IN_REVIEW))
                .limit(dto.maxReviewWordsPerDay())
                .collect(Collectors.toSet());

        List<Word> listOfWords = new ArrayList<>();
        listOfWords.addAll(newWords);
        listOfWords.addAll(reviewWords);

        listOfWords.forEach(word -> {
            word.setOccurrence(0);
            word.setCurrentStreak(0);
        });
        return new Review(
                dto.maxNewWordsPerDay(),
                dto.maxReviewWordsPerDay(),
                wordPack,
                listOfWords
        );
    }
}
