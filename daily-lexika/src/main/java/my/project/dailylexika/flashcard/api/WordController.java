package my.project.dailylexika.flashcard.api;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.enumerations.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/words")
public class WordController {

    private final WordService wordService;

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<WordDto>> getAllWordsByStatus(@PathVariable("status") Status status,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return ResponseEntity.ok(wordService.getPageByStatus(status, PageRequest.of(page, size)));
    }

    @GetMapping("/word-of-the-day")
    public ResponseEntity<WordDto> getWordOfTheDay() {
        return ResponseEntity.ok(wordService.getWordOfTheDay());
    }

    @GetMapping("/by-word-data/{id}")
    public ResponseEntity<WordDto> getWordByWordDataId(@PathVariable("id") Integer wordDataId) {
        return ResponseEntity.ok(wordService.getByWordDataId(wordDataId));
    }
}
