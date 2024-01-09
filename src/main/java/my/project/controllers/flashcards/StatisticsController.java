package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.ResponseDTO;
import my.project.services.flashcards.StatisticsService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getStatistics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.statistics.getStatistics", null, Locale.getDefault()))
                        .data(Map.of("statisticsDTO", statisticsService.getStatistics()))
                        .build());
    }
}
