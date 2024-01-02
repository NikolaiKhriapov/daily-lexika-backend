package my.project.services.flashcards;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlashcardsService {

    private final WordService wordService;
    private final ReviewService reviewService;

    @Transactional
    public void deleteFlashcardsForUser(Long userId) {
        reviewService.deleteAllByUserId(userId);
        wordService.deleteAllByUserId(userId);
    }
}
