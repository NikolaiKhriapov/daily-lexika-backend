package my.project.dailylexika.flashcard.api;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.flashcard.service.StatisticsService;
import my.project.library.dailylexika.dtos.flashcards.StatisticsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsDto> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
