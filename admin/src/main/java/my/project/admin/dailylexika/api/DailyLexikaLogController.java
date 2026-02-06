package my.project.admin.dailylexika.api;

import lombok.RequiredArgsConstructor;
import my.project.admin.dailylexika.service.DailyLexikaLogService;
import my.project.library.dailylexika.dtos.log.LogDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/daily-lexika/logs")
@RequiredArgsConstructor
public class DailyLexikaLogController {

    private final DailyLexikaLogService dailyLexikaLogService;

    @GetMapping
    public ResponseEntity<Page<LogDto>> getAllLogs(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @RequestParam(name = "sort", defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(dailyLexikaLogService.getAllLogs(page, size, sortDirection));
    }
}
