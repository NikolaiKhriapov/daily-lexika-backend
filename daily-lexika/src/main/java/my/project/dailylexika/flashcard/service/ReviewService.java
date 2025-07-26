package my.project.dailylexika.flashcard.service;

import my.project.dailylexika.flashcard.model.entities.Word;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.library.dailylexika.dtos.flashcards.ReviewDto;
import my.project.library.dailylexika.dtos.flashcards.ReviewStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    ReviewDto updateReview(Long reviewId, ReviewDto reviewDto);
    ReviewDto createReview(ReviewDto reviewDto);
    ReviewDto refreshReview(Long reviewId);
    void deleteReview(Long reviewId);
    ReviewDto processReviewAction(Long reviewId, Boolean isCorrect);
    ReviewStatisticsDto getReviewStatistics(Long reviewId);
    List<Word> generateListOfWordsForReview(WordPack wordPack, ReviewDto reviewDto);
    void deleteAllByUserIdAndPlatform(Integer userId, Platform platform);
    void updateUserStreak();
    void throwIfReviewExistsForWordPack(String wordPackName);
}
