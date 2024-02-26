package my.project.controllers.flashcards;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.models.dtos.flashcards.ReviewDto;
import my.project.services.flashcards.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable("reviewId") Long reviewId,
                                                  @RequestBody @Valid ReviewDto reviewDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateReview(reviewId, reviewDTO));
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody @Valid ReviewDto reviewDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewDTO));
    }

    @PatchMapping("/refresh/{reviewId}")
    public ResponseEntity<ReviewDto> refreshReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.refreshReview(reviewId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{reviewId}/action")
    public ResponseEntity<ReviewDto> processReviewAction(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "answer", required = false) Boolean isCorrect
    ) {
        return ResponseEntity.ok(reviewService.processReviewAction(reviewId, isCorrect));
    }
}
