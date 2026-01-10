package my.project.admin.dailylexika.api;

import lombok.RequiredArgsConstructor;
import my.project.admin.dailylexika.service.DailyLexikaUserService;
import my.project.library.dailylexika.dtos.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/daily-lexika/users")
@RequiredArgsConstructor
public class DailyLexikaUserController {

    private final DailyLexikaUserService dailyLexikaUserService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestParam(name = "sort", defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(dailyLexikaUserService.getAllUsers(page, size, sortDirection));
    }
}
