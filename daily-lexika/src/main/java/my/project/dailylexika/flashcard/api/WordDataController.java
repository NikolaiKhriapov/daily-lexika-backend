package my.project.dailylexika.flashcard.api;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-data")
public class WordDataController {

    private final WordDataService wordDataService;

    @GetMapping
    public ResponseEntity<List<WordDataDto>> getAllWordData() {
        return ResponseEntity.ok(wordDataService.getAll());
    }

    @GetMapping("/{id}/add-word-pack/{wordPackName}")
    public ResponseEntity<WordDataDto> addCustomWordPackToWordData(@PathVariable("id") Integer wordDataId,
                                                                   @PathVariable("wordPackName") String wordPackName) {
        return ResponseEntity.ok(wordDataService.addCustomWordPackToWordData(wordDataId, wordPackName));
    }

    @GetMapping("/{wordPackNameOriginal}/add-word-pack-to-word-data/{wordPackNameToBeAdded}")
    public ResponseEntity<Void> addCustomWordPackToWordDataByWordPackName(@PathVariable("wordPackNameToBeAdded") String wordPackNameToBeAdded,
                                                                          @PathVariable("wordPackNameOriginal") String wordPackNameOriginal) {
        wordDataService.addCustomWordPackToWordDataByWordPackName(wordPackNameToBeAdded, wordPackNameOriginal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/remove-word-pack/{wordPackName}")
    public ResponseEntity<WordDataDto> removeCustomWordPackFromWordData(@PathVariable("id") Integer wordDataId,
                                                                        @PathVariable("wordPackName") String wordPackName) {
        return ResponseEntity.ok(wordDataService.removeCustomWordPackFromWordData(wordDataId, wordPackName));
    }
}
