package my.project.admin.dailylexika.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.admin.dailylexika.service.DailyLexikaWordDataService;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/daily-lexika/word-data")
@RequiredArgsConstructor
public class DailyLexikaWordDataController {

    private final DailyLexikaWordDataService dailyLexikaWordDataService;

    @GetMapping
    public ResponseEntity<Page<WordDataDto>> getWordDataPage(@RequestParam("platform") Platform platform,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size,
                                                             @RequestParam(name = "query", required = false) String query) {
        return ResponseEntity.ok(dailyLexikaWordDataService.getWordDataPage(platform, page, size, query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordDataDto> getWordData(@PathVariable Integer id) {
        return ResponseEntity.ok(dailyLexikaWordDataService.getWordData(id));
    }

    @PostMapping
    public ResponseEntity<WordDataDto> createWordData(@RequestBody @Valid WordDataCreateDto createDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dailyLexikaWordDataService.createWordData(createDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WordDataDto> updateWordData(@PathVariable Integer id,
                                                      @RequestBody @Valid WordDataUpdateDto patchDto) {
        return ResponseEntity.ok(dailyLexikaWordDataService.updateWordData(id, patchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWordData(@PathVariable Integer id) {
        dailyLexikaWordDataService.deleteWordData(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
