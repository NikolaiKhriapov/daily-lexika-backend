package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.ReviewDTO;
import my.project.services.flashcards.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable("reviewId") Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO newReviewDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(newReviewDTO));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<Void> refreshReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.refreshReview(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{reviewId}/action")
    public ResponseEntity<Map<String, Object>> processReviewAction(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam(value = "answer", required = false) Boolean isCorrect
    ) {
        return ResponseEntity.ok(reviewService.processReviewAction(reviewId, isCorrect));
    }
}
