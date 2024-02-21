package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
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
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable("reviewId") Long reviewId,
                                                  @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.updateReview(reviewId, reviewDTO));
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewDTO));
    }

    @PatchMapping("/refresh/{reviewId}")
    public ResponseEntity<ReviewDTO> refreshReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(reviewService.refreshReview(reviewId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{reviewId}/action")
    public ResponseEntity<ReviewDTO> processReviewAction(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "answer", required = false) Boolean isCorrect
    ) {
        return ResponseEntity.ok(reviewService.processReviewAction(reviewId, isCorrect));
    }
}
