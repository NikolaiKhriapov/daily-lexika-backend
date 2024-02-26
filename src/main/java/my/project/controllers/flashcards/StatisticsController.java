package my.project.controllers.flashcards;

import lombok.RequiredArgsConstructor;
import my.project.models.dtos.flashcards.StatisticsDto;
import my.project.services.flashcards.StatisticsService;
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
