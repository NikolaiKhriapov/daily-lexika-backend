package my.project.dailylexika.log.api;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.log.service.LogService;
import my.project.library.dailylexika.dtos.log.LogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    @PreAuthorize("hasRole(@AR.SUPER_ADMIN)")
    public ResponseEntity<Page<LogDto>> getPageOfLogs(@RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam(name = "sort", defaultValue = "desc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(Sort.Direction.DESC, "id")
                : Sort.by(Sort.Direction.ASC, "id");

        return ResponseEntity.ok(logService.getPage(PageRequest.of(page, size, sort)));
    }
}
