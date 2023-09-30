package my.project.vocabulary.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChineseFlashcardsService {

    private final WordService wordService;
    private final ReviewService reviewService;

    @Transactional
    public void deleteChineseFlashcardsForUser(Long userId) {
        reviewService.deleteAllByUserId(userId);
        wordService.deleteAllByUserId(userId);
    }
}
