package my.project.dailylexika.flashcard.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.dailylexika.flashcard.service.WordService;
import my.project.library.dailylexika.dtos.flashcards.WordDto;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackDto;
import org.springframework.data.domain.Page;
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
    private final WordDataService wordDataService;
    private final WordService wordService;

    @GetMapping
    public ResponseEntity<List<WordPackDto>> getAllWordPacks() {
        return ResponseEntity.ok(wordPackService.getAllForUser());
    }

    //TODO::: move to WordController and rename
    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<Page<WordDto>> getPageOfWordsByWordPackName(@PathVariable("wordPackName") String wordPackName,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size) {
        return ResponseEntity.ok(wordService.getPageByWordPackName(wordPackName, PageRequest.of(page, size)));
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

    //TODO::: move to WordDataController and rename
    @GetMapping("/{wordPackName}/add-word/{wordDataId}")
    public ResponseEntity<WordDataDto> addCustomWordPackToWordData(@PathVariable("wordPackName") String wordPackName,
                                                                   @PathVariable("wordDataId") Integer wordDataId) {
        return ResponseEntity.ok(wordDataService.addCustomWordPackToWordData(wordDataId, wordPackName));
    }

    //TODO::: move to WordDataController and rename
    @GetMapping("/{wordPackNameTo}/add-words-from-wordpack/{wordPackNameFrom}")
    public ResponseEntity<Void> addCustomWordPackToWordDataByWordPackName(@PathVariable("wordPackNameTo") String wordPackNameTo,
                                                                          @PathVariable("wordPackNameFrom") String wordPackNameFrom) {
        wordDataService.addCustomWordPackToWordDataByWordPackName(wordPackNameTo, wordPackNameFrom);
        return ResponseEntity.ok().build();
    }

    //TODO::: move to WordDataController and rename
    @GetMapping("/{wordPackName}/remove-word/{wordDataId}")
    public ResponseEntity<WordDataDto> removeCustomWordPackFromWordData(@PathVariable("wordPackName") String wordPackName,
                                                                        @PathVariable("wordDataId") Integer wordDataId) {
        return ResponseEntity.ok(wordDataService.removeCustomWordPackFromWordData(wordDataId, wordPackName));
    }
}
