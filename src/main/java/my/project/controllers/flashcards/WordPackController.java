package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.ResponseDTO;
import my.project.models.dto.flashcards.WordDTO;
import my.project.services.flashcards.WordPackService;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flashcards/word-packs")
public class WordPackController {

    private final WordPackService wordPackService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllWordPacks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getAllWordPacks", null, Locale.getDefault()))
                        .data(Map.of("allWordPacksDTO", wordPackService.getAllWordPacks()))
                        .build());
    }

    @GetMapping("/{wordPackName}")
    public ResponseEntity<ResponseDTO> getWordPack(@PathVariable("wordPackName") String wordPackName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getWordPack", null, Locale.getDefault()))
                        .data(Map.of("wordPackDTO", wordPackService.getWordPackDTOByName(wordPackName)))
                        .build());
    }

    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<ResponseDTO> getAllWordsForWordPack(
            @PathVariable("wordPackName") String wordPackName,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<WordDTO> allWordsForWordPack = wordPackService.getAllWordsForWordPack(wordPackName, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getAllWordsForWordPacks", null, Locale.getDefault()))
                        .data(Map.of("allWordsForWordPackDTO", allWordsForWordPack))
                        .build());
    }
}
