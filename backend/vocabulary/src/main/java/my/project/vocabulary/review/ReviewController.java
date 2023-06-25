package my.project.vocabulary.review;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.dto.Response;
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
    public ResponseEntity<Response> getAllReviews() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getAllReviews", null, Locale.getDefault()))
                        .data(Map.of("allReviews", reviewService.getAllReviews()))
                        .build());
    }

    @PostMapping
    public ResponseEntity<Response> createReview(@RequestBody ReviewDTO newReviewDTO) {
        reviewService.createReview(newReviewDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage("response.review.createReview", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Response> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message(messageSource.getMessage("response.review.deleteReview", null, Locale.getDefault()))
                        .build());
    }

    @GetMapping("/{reviewId}/start")
    public ResponseEntity<Response> startReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.startReview", null, Locale.getDefault()))
                        .data(Map.of("reviewWord", reviewService.showOneReviewWord(reviewId)))
                        .build());
    }

    @GetMapping("/{reviewId}/next")
    public ResponseEntity<Response> nextReview(@PathVariable("reviewId") Long reviewId,
                                                @RequestParam(value = "answer") String answer) {
        reviewService.considerAnswer(reviewId, answer);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.startReview", null, Locale.getDefault()))
                        .data(Map.of("reviewWord", reviewService.showOneReviewWord(reviewId)))
                        .build());
    }
}
