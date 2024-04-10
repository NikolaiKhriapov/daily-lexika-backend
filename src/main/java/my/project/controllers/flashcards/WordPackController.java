package my.project.controllers.flashcards;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.models.dtos.flashcards.WordDto;
import my.project.models.dtos.flashcards.WordDataDto;
import my.project.models.dtos.flashcards.WordPackDto;
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
    public ResponseEntity<List<WordPackDto>> getAllWordPacks() {
        return ResponseEntity.ok(wordPackService.getAllWordPacksForUser());
    }

    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<List<WordDto>> getAllWordsForWordPack(@PathVariable("wordPackName") String wordPackName,
                                                                @RequestParam("page") int page,
                                                                @RequestParam("size") int size) {
        return ResponseEntity.ok(wordPackService.getAllWordsForWordPack(wordPackName, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<Void> createCustomWordPack(@RequestBody @Valid WordPackDto wordPackDto) {
        wordPackService.createCustomWordPack(wordPackDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{wordPackName}")
    public ResponseEntity<Void> deleteCustomWordPack(@PathVariable("wordPackName") String wordPackName) {
        wordPackService.deleteCustomWordPack(wordPackName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{wordPackName}/add-word/{wordDataId}")
    public ResponseEntity<WordDataDto> addWordToCustomWordPack(@PathVariable("wordPackName") String wordPackName,
                                                               @PathVariable("wordDataId") Long wordDataId) {
        return ResponseEntity.ok(wordPackService.addWordToCustomWordPack(wordPackName, wordDataId));
    }

    @GetMapping("/{wordPackName}/remove-word/{wordDataId}")
    public ResponseEntity<WordDataDto> removeWordFromCustomWordPack(@PathVariable("wordPackName") String wordPackName,
                                                                    @PathVariable("wordDataId") Long wordDataId) {
        return ResponseEntity.ok(wordPackService.removeWordFromCustomWordPack(wordPackName, wordDataId));
    }
}
