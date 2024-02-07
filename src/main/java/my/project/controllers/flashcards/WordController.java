package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.flashcards.WordDTO;
import my.project.models.entity.enumeration.Status;
import my.project.services.flashcards.WordService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/words")
public class WordController {

    private final WordService wordService;

    @GetMapping("/status/{status}")
    public ResponseEntity<List<WordDTO>> getAllWordsByStatus(
            @PathVariable("status") Status status,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(wordService.getAllWordsByStatus(status, PageRequest.of(page, size)));
    }
}
