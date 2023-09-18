package my.project.vocabulary.controller;

import lombok.RequiredArgsConstructor;
import my.project.vocabulary.model.dto.ResponseDTO;
import my.project.vocabulary.service.WordService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocabulary/words")
public class WordController {

    private final WordService wordService;
    private final MessageSource messageSource;

    @GetMapping("/statistics")
    public ResponseEntity<ResponseDTO> getWordStatistics(@RequestHeader("userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.word.getWordStatistics", null, Locale.getDefault()))
                        .data(Map.of("wordStatisticsDTO", wordService.getWordStatistics(userId)))
                        .build());
    }
}