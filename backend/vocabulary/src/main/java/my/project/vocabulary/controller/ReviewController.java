package my.project.vocabulary.controller;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ResponseWrapper;
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
    public ResponseEntity<ResponseWrapper> getAllReviews() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.getAllReviews", null, Locale.getDefault()))
                        .data(Map.of("allReviews", reviewService.getAllReviews()))
                        .build());
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper> createReview(@RequestBody ReviewDTO newReviewDTO) {
        reviewService.createReview(newReviewDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage("response.review.createReview", null, Locale.getDefault()))
                        .build());
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ResponseWrapper> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .message(messageSource.getMessage("response.review.deleteReview", null, Locale.getDefault()))
                        .build());
    }

    @GetMapping("/{reviewId}/action")
    public ResponseEntity<ResponseWrapper> processReviewAction(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "answer", required = false) String answer
    ) {
        reviewService.processReviewAction(reviewId, answer);

        WordDTO reviewWord = reviewService.showOneReviewWord(reviewId);
        Map<String, WordDTO> data = (reviewWord != null) ? Map.of("reviewWord", reviewWord) : null;
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.review.startReview", null, Locale.getDefault()))
                        .data(data)
                        .build());
    }
}