package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.dto.flashcards.WordPackDTO;
import my.project.services.flashcards.WordPackService;
import org.springframework.data.domain.PageRequest;
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
        return ResponseEntity.ok(wordPackService.getAllWordPacks());
    }

    @GetMapping("/{wordPackName}")
    public ResponseEntity<WordPackDTO> getWordPack(@PathVariable("wordPackName") String wordPackName) {
        return ResponseEntity.ok(wordPackService.getWordPackDTOByName(wordPackName));
    }

    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<List<WordDTO>> getAllWordsForWordPack(
            @PathVariable("wordPackName") String wordPackName,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(wordPackService.getAllWordsForWordPack(wordPackName, PageRequest.of(page, size)));
    }
}
