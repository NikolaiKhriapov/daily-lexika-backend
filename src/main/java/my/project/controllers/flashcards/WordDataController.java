package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dtos.flashcards.WordDataDto;
import my.project.services.flashcards.WordDataService;
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
        return ResponseEntity.ok(wordDataService.getAllWordData());
    }
}
