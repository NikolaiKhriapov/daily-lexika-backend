package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordDataDTO;
import my.project.services.flashcards.WordDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-data")
public class WordDataController {

    private final WordDataService wordDataService;

    @GetMapping("/search/{searchQuery}")
    public ResponseEntity<List<WordDataDTO>> search(@PathVariable("searchQuery") String searchQuery) {
        return ResponseEntity.ok(wordDataService.search(searchQuery));
    }
}
