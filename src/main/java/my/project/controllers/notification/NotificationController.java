package my.project.controllers.notification;

import lombok.AllArgsConstructor;
import my.project.models.dto.ResponseDTO;
import my.project.services.notification.NotificationService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllNotifications() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.notification.getAllNotifications", null, Locale.getDefault()))
                        .data(Map.of("allNotificationsDTO", notificationService.getAllNotifications()))
                        .build());
    }

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<ResponseDTO> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.notification.readNotification", null, Locale.getDefault()))
                        .build());
    }
}

