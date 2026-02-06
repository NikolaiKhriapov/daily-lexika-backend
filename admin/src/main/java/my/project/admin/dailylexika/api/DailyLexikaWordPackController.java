package my.project.admin.dailylexika.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.admin.dailylexika.service.DailyLexikaWordPackService;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/daily-lexika/word-packs")
@RequiredArgsConstructor
public class DailyLexikaWordPackController {

    private final DailyLexikaWordPackService dailyLexikaWordPackService;

    @GetMapping
    public ResponseEntity<Page<WordPackDto>> getWordPackPage(@RequestParam("platform") Platform platform,
                                                             @RequestParam("page") int page,
                                                             @RequestParam("size") int size) {
        return ResponseEntity.ok(dailyLexikaWordPackService.getWordPackPage(platform, page, size));
    }

    @GetMapping("/{wordPackId}")
    public ResponseEntity<WordPackDto> getWordPack(@PathVariable Long wordPackId) {
        return ResponseEntity.ok(dailyLexikaWordPackService.getWordPack(wordPackId));
    }

    @PostMapping
    public ResponseEntity<WordPackDto> createWordPack(@RequestBody @Valid WordPackCreateDto createDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dailyLexikaWordPackService.createWordPack(createDto));
    }

    @PatchMapping("/{wordPackId}")
    public ResponseEntity<WordPackDto> updateWordPack(@PathVariable Long wordPackId,
                                                      @RequestBody @Valid WordPackUpdateDto patchDto) {
        return ResponseEntity.ok(dailyLexikaWordPackService.updateWordPack(wordPackId, patchDto));
    }

    @DeleteMapping("/{wordPackId}")
    public ResponseEntity<Void> deleteWordPack(@PathVariable Long wordPackId) {
        dailyLexikaWordPackService.deleteWordPack(wordPackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
