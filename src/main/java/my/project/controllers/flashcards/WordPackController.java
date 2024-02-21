package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.dto.flashcards.WordDataDTO;
import my.project.models.dto.flashcards.WordPackDTO;
import my.project.services.flashcards.WordPackService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-packs")
public class WordPackController {

    private final WordPackService wordPackService;

    @GetMapping
    public ResponseEntity<List<WordPackDTO>> getAllWordPacks() {
        return ResponseEntity.ok(wordPackService.getAllWordPacksForUser());
    }

    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<List<WordDTO>> getAllWordsForWordPack(@PathVariable("wordPackName") String wordPackName,
                                                                @RequestParam("page") int page,
                                                                @RequestParam("size") int size) {
        return ResponseEntity.ok(wordPackService.getAllWordsForWordPack(wordPackName, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<Void> createCustomWordPack(@RequestBody WordPackDTO wordPackDTO) {
        wordPackService.createCustomWordPack(wordPackDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{wordPackName}")
    public ResponseEntity<Void> deleteCustomWordPack(@PathVariable("wordPackName") String wordPackName) {
        wordPackService.deleteCustomWordPack(wordPackName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{wordPackName}/add-word/{wordDataId}")
    public ResponseEntity<WordDataDTO> addWordToCustomWordPack(@PathVariable("wordPackName") String wordPackName,
                                                               @PathVariable("wordDataId") Long wordDataId) {
        return ResponseEntity.ok(wordPackService.addWordToCustomWordPack(wordPackName, wordDataId));
    }

    @GetMapping("/{wordPackName}/remove-word/{wordDataId}")
    public ResponseEntity<WordDataDTO> removeWordFromCustomWordPack(@PathVariable("wordPackName") String wordPackName,
                                                                    @PathVariable("wordDataId") Long wordDataId) {
        return ResponseEntity.ok(wordPackService.removeWordFromCustomWordPack(wordPackName, wordDataId));
    }
}
