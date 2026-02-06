package my.project.dailylexika.flashcard.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordDataService;
import my.project.library.dailylexika.dtos.flashcards.WordDataDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordDataUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-data")
public class WordDataController {

    private final WordDataService wordDataService;

    @GetMapping("/search")
    public ResponseEntity<List<WordDataDto>> searchWordData(@RequestParam("query") String query,
                                                            @RequestParam("limit") Integer limit) {
        return ResponseEntity.ok(wordDataService.search(query, limit));
    }

    @GetMapping("/search/admin")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Page<WordDataDto>> searchWordDataAdmin(@RequestParam("platform") Platform platform,
                                                                 @RequestParam("page") int page,
                                                                 @RequestParam("size") int size,
                                                                 @RequestParam(name = "query", required = false) String query) {
        return ResponseEntity.ok(wordDataService.getPage(platform, query, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordDataDto> getWordData(@PathVariable Integer id) {
        return ResponseEntity.ok(wordDataService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordDataDto> createWordData(@RequestBody @Valid WordDataCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordDataService.create(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordDataDto> updateWordData(@PathVariable Integer id,
                                                      @RequestBody @Valid WordDataUpdateDto dto) {
        return ResponseEntity.ok(wordDataService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Void> deleteWordData(@PathVariable Integer id) {
        wordDataService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/add-word-pack/{wordPackId}")
    public ResponseEntity<WordDataDto> addCustomWordPackToWordData(@PathVariable Integer id,
                                                                   @PathVariable Long wordPackId) {
        return ResponseEntity.ok(wordDataService.addCustomWordPackToWordData(id, wordPackId));
    }

    @GetMapping("/{wordPackIdOriginal}/add-word-pack-to-word-data/{wordPackIdToBeAdded}")
    public ResponseEntity<Void> addCustomWordPackToWordDataByWordPackId(@PathVariable Long wordPackIdToBeAdded,
                                                                        @PathVariable Long wordPackIdOriginal) {
        wordDataService.addCustomWordPackToWordDataByWordPackId(wordPackIdToBeAdded, wordPackIdOriginal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/remove-word-pack/{wordPackId}")
    public ResponseEntity<WordDataDto> removeCustomWordPackFromWordData(@PathVariable Integer id,
                                                                        @PathVariable Long wordPackId) {
        return ResponseEntity.ok(wordDataService.removeCustomWordPackFromWordData(id, wordPackId));
    }
}
