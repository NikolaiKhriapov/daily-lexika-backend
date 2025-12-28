package my.project.dailylexika.flashcard.listener;

import lombok.AllArgsConstructor;
import my.project.dailylexika.flashcard.model.entities.WordData;
import my.project.dailylexika.flashcard.model.entities.WordPack;
import my.project.dailylexika.flashcard.service.ReviewService;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.library.dailylexika.events.flashcard.CustomWordPackToBeDeletedEvent;
import my.project.library.dailylexika.events.user.AccountDeletedEvent;
import my.project.library.dailylexika.events.user.AccountRegisteredEvent;
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
    public void on(CustomWordPackToBeDeletedEvent event) {
        WordPack wordPack = wordPackService.getByName(event.wordPackName());

        reviewService.deleteReviewIfExistsForWordPack(event.wordPackName());

        List<WordData> listOfWordData = wordDataService.getAllByWordPackNameAndPlatform(wordPack.getName(), event.platform());
        listOfWordData.forEach(wordData -> {
            List<WordPack> listOfWordPacks = wordData.getListOfWordPacks();
            listOfWordPacks.remove(wordPack);
            wordData.setListOfWordPacks(listOfWordPacks);
        });
    }
}
