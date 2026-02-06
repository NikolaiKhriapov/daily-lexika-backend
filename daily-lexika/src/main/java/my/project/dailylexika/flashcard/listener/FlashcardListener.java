package my.project.dailylexika.flashcard.listener;

import lombok.AllArgsConstructor;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.library.dailylexika.events.flashcard.WordDataToBeDeletedEvent;
import my.project.library.dailylexika.events.flashcard.WordPackToBeDeletedEvent;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.AccountRegisteredEvent;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@AllArgsConstructor
public class FlashcardListener {

    private final ReviewService reviewService;
    private final WordService wordService;
    private final WordDataService wordDataService;
    private final WordPackService wordPackService;

    @EventListener
    @Transactional
    public void on(AccountRegisteredEvent event) {
        wordService.createAllWordsForUserAndPlatform(event.userId(), event.platform());
    }

    @EventListener
    @Transactional
    public void on(AccountDeletedEvent event) {
        reviewService.deleteAllByUserIdAndPlatform(event.userId(), event.platform());
        wordService.deleteAllByUserIdAndPlatform(event.userId(), event.platform());
        wordPackService.deleteAllByUserIdAndPlatform(event.userId(), event.platform());
    }

    @EventListener
    @Transactional
    public void on(WordDataToBeDeletedEvent event) {
        reviewService.deleteReviewWordLinksByWordDataId(event.wordDataId());
        wordService.deleteAllByWordDataId(List.of(event.wordDataId()));
    }

    @EventListener
    @Transactional
    public void on(WordPackToBeDeletedEvent event) {
        handleWordPackDeletion(event.wordPackId(), event.platform());
    }

    private void handleWordPackDeletion(Long wordPackId, Platform platform) {
        boolean isWordPackCustom = wordPackService.getById(wordPackId).getUserId() != null;
        if (isWordPackCustom) {
            reviewService.deleteReviewIfExistsForWordPack(wordPackId);
        } else {
            reviewService.deleteAllByWordPackId(wordPackId);
        }
        wordDataService.removeWordPackReferences(wordPackId, platform);
    }
}
