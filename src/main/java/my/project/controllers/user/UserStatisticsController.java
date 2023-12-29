package my.project.controllers.user;

import lombok.RequiredArgsConstructor;
import my.project.models.dto.ResponseDTO;
import my.project.services.user.UserStatisticsService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user/statistics")
@RequiredArgsConstructor
public class UserStatisticsController {

    private final UserStatisticsService userStatisticsService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getUserStatistics() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userStatistics.getUserStatistics", null, Locale.getDefault()))
                        .data(Map.of("userStatisticsDTO", userStatisticsService.getUserStatistics()))
                        .build());
    }

    @GetMapping("/streak")
    public ResponseEntity<ResponseDTO> updateUserStreak() {
        userStatisticsService.updateUserStreak();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage(
                                "response.userStatistics.updateUserStreak", null, Locale.getDefault()))
                        .build());
    }
}