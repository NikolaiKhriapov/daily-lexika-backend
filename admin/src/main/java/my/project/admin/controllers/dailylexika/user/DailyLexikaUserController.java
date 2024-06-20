package my.project.admin.controllers.dailylexika.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import my.project.admin.services.user.dailylexika.user.DailyLexikaUserService;
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
    public ResponseEntity<Page<UserDto>> getAllUsers(HttpServletRequest request,
                                                     @RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestParam(name = "sort", defaultValue = "asc") String sortDirection) {
        return ResponseEntity.ok(dailyLexikaUserService.getAllUsers(request, page, size, sortDirection));
    }
}
