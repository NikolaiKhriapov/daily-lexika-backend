package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dtos.flashcards.WordDto;
import my.project.models.entities.enumerations.Status;
import my.project.services.flashcards.WordService;
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
        return ResponseEntity.ok(wordService.getPageOfWordsByStatus(status, PageRequest.of(page, size)));
    }

    @GetMapping("/word-of-the-day")
    public ResponseEntity<WordDto> getWordOfTheDay() {
        return ResponseEntity.ok(wordService.getWordOfTheDay());
    }

    @GetMapping("/by-word-data/{id}")
    public ResponseEntity<WordDto> getWordByWordDataId(@PathVariable("id") Integer wordDataId) {
        return ResponseEntity.ok(wordService.findByWordDataId(wordDataId));
    }
}
