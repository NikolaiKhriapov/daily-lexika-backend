package my.project.controllers.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.chineseflashcards.ResponseDTO;
import my.project.models.dto.chineseflashcards.ReviewDTO;
import my.project.services.chineseflashcards.ReviewService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chinese-flashcards/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllReviews() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getAllReviews", null, Locale.getDefault()))
                        .data(Map.of("allReviewsDTO", reviewService.getAllReviews()))
                        .build());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> getReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getReview", null, Locale.getDefault()))
                        .data(Map.of("reviewDTO", reviewService.getReviewById(reviewId)))
                        .build());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createReview(@RequestBody ReviewDTO newReviewDTO) {
        reviewService.createReview(newReviewDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage("response.review.createReview", null, Locale.getDefault()))
                        .build());
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> refreshReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.refreshReview(reviewId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.refreshReview", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseDTO> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message(messageSource.getMessage("response.review.deleteReview", null, Locale.getDefault()))
                        .build());
    }

    @GetMapping("/{reviewId}/action")
    public ResponseEntity<ResponseDTO> processReviewAction(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "answer", required = false) String answer
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.startReview", null, Locale.getDefault()))
                        .data(reviewService.processReviewAction(reviewId, answer))
                        .build());
    }

    @GetMapping("/{reviewId}/words")
    public ResponseEntity<ResponseDTO> getAllWordsForReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getAllWordsForReview", null, Locale.getDefault()))
                        .data(Map.of("wordsForReviewDTO", reviewService.getAllWordsForReview(reviewId)))
                        .build());
    }

    @GetMapping("/statistics/{reviewId}")
    public ResponseEntity<ResponseDTO> getReviewStatistics(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getReviewStatistics", null, Locale.getDefault()))
                        .data(Map.of("reviewStatisticsDTO", reviewService.getReviewStatistics(reviewId)))
                        .build());
    }
}