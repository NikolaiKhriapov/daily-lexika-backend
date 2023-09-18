package my.project.vocabulary.controller;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ResponseDTO;
import my.project.vocabulary.service.WordPackService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocabulary/word-packs")
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
                                "response.wordPack.getAllWordPacks", null, Locale.getDefault()))
                        .data(Map.of("wordPackDTO", wordPackService.getWordPackDTOByName(wordPackName)))
                        .build());
    }

    @GetMapping("/{wordPackName}/words")
    public ResponseEntity<ResponseDTO> getAllWordsForWordPack(@PathVariable("wordPackName") String wordPackName,
                                                              @RequestHeader("userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getAllWordsForWordPacks", null, Locale.getDefault()))
                        .data(Map.of("allWordsForWordPackDTO", wordPackService.getAllWordsForWordPack(wordPackName, userId)))
                        .build());
    }

}
