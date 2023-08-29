package my.project.vocabulary.controller;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ResponseWrapper;
import my.project.vocabulary.service.WordPackService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseWrapper> getAllWordPacks() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getAllWordPacks", null, Locale.getDefault()))
                        .data(Map.of("allWordPacks", wordPackService.getAllWordPacks()))
                        .build());
    }

    @GetMapping("/{wordPackName}")
    public ResponseEntity<ResponseWrapper> getAllWordsForWordPack(@PathVariable("wordPackName") String wordPackName) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseWrapper.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.wordPack.getAllWordsForWordPacks", null, Locale.getDefault()))
                        .data(Map.of("allWordsForWordPack", wordPackService.getAllWordsForWordPack(wordPackName)))
                        .build());
    }

}
