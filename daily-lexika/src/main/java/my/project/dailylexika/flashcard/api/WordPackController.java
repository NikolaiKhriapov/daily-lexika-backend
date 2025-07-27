package my.project.dailylexika.flashcard.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
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
        return ResponseEntity.ok(wordPackService.getAllForUser());
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
}
