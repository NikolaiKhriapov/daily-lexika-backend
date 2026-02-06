package my.project.dailylexika.flashcard.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.WordPackService;
import my.project.library.dailylexika.dtos.flashcards.WordPackCustomCreateDto;
import my.project.library.dailylexika.dtos.flashcards.WordPackUserDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackCreateDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackDto;
import my.project.library.dailylexika.dtos.flashcards.admin.WordPackUpdateDto;
import my.project.library.dailylexika.enumerations.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-packs")
public class WordPackController {

    private final WordPackService wordPackService;

    @GetMapping
    public ResponseEntity<List<WordPackUserDto>> getAllWordPacks() {
        return ResponseEntity.ok(wordPackService.getAllForUser());
    }

    @GetMapping("/search/admin")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Page<WordPackDto>> searchWordPacksAdmin(@RequestParam("platform") Platform platform,
                                                                  @RequestParam("page") int page,
                                                                  @RequestParam("size") int size) {
        return ResponseEntity.ok(wordPackService.getPage(platform, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordPackDto> getWordPack(@PathVariable Long id) {
        return ResponseEntity.ok(wordPackService.getDtoById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordPackDto> createWordPack(@RequestBody @Valid WordPackCreateDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wordPackService.create(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<WordPackDto> updateWordPack(@PathVariable Long id,
                                                      @RequestBody @Valid WordPackUpdateDto dto) {
        return ResponseEntity.ok(wordPackService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Void> deleteWordPack(@PathVariable Long id) {
        wordPackService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/custom")
    public ResponseEntity<WordPackDto> createCustomWordPack(@RequestBody @Valid WordPackCustomCreateDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(wordPackService.createCustomWordPack(dto));
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<Void> deleteCustomWordPack(@PathVariable Long id) {
        wordPackService.deleteCustomWordPack(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
