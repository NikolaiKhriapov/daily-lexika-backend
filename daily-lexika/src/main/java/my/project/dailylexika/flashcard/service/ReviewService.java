package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    ReviewDto createReview(@NotNull @Valid ReviewDto reviewDto);
    ReviewDto updateReview(@NotNull Long reviewId, @NotNull @Valid ReviewDto reviewDto);
    void deleteReview(@NotNull Long reviewId);
    ReviewStatisticsDto getReviewStatistics(@NotNull Long reviewId);
    List<Word> generateListOfWordsForReview(@NotNull WordPack wordPack, @NotNull @Valid ReviewDto reviewDto);
    ReviewDto refreshReview(@NotNull Long reviewId);
    ReviewDto processReviewAction(@NotNull Long reviewId, Boolean isCorrect);
    void updateUserStreak();
    void deleteAllByUserIdAndPlatform(@NotNull Integer userId, @NotNull Platform platform);
    void deleteReviewIfExistsForWordPack(@NotBlank String wordPackName);
}
