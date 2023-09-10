package my.project.vocabulary.controller;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ResponseDTO;
import my.project.vocabulary.model.dto.ReviewDTO;
import my.project.vocabulary.model.dto.WordDTO;
import my.project.vocabulary.service.ReviewService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocabulary/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllReviews(@RequestHeader("userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getAllReviews", null, Locale.getDefault()))
                        .data(Map.of("allReviews", reviewService.getAllReviews(userId)))
                        .build());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createReview(@RequestBody ReviewDTO newReviewDTO,
                                                    @RequestHeader("userId") Long userId) {
        reviewService.createReview(newReviewDTO, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage("response.review.createReview", null, Locale.getDefault()))
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
        WordDTO reviewWord = reviewService.processReviewAction(reviewId, answer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.startReview", null, Locale.getDefault()))
                        .data((reviewWord != null) ? Map.of("reviewWord", reviewWord) : null)
                        .build());
    }
}