package my.project.controllers.chineseflashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.chineseflashcards.ResponseDTO;
import my.project.services.chineseflashcards.WordService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chinese-flashcards/words")
public class WordController {

    private final WordService wordService;
    private final MessageSource messageSource;

    @GetMapping("/statistics")
    public ResponseEntity<ResponseDTO> getWordStatistics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.word.getWordStatistics", null, Locale.getDefault()))
                        .data(Map.of("wordStatisticsDTO", wordService.getWordStatistics()))
                        .build());
    }
}