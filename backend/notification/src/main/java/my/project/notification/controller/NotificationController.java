package my.project.notification.controller;

import lombok.AllArgsConstructor;
import my.project.clients.notification.NotificationRequest;
import my.project.notification.model.dto.ResponseDTO;
import my.project.notification.service.NotificationService;
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
    public ResponseEntity<ResponseDTO> getAllNotifications(@RequestHeader("userId") Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.notification.getAllNotifications", null, Locale.getDefault()))
                        .data(Map.of("allNotificationsDTO", notificationService.getAllNotifications(userId)))
                        .build());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.sendNotification(notificationRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.CREATED.value())
                        .message(messageSource.getMessage("response.notification.sendNotification", null, Locale.getDefault()))
                        .build());
    }

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<ResponseDTO> readNotification(@RequestHeader("userId") Long userId,
                                                        @PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(userId, notificationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDTO.builder()
                        .timeStamp(LocalDateTime.now())
                        .statusCode(HttpStatus.OK.value())
                        .message(messageSource.getMessage("response.notification.readNotification", null, Locale.getDefault()))
                        .build());
    }
}

