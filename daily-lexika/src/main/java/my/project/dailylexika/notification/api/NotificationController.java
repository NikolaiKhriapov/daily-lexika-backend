package my.project.dailylexika.notification.api;

import lombok.AllArgsConstructor;
import my.project.library.dailylexika.dtos.notification.NotificationDto;
import my.project.dailylexika.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<Void> readNotification(@PathVariable("notificationId") Integer notificationId) {
        notificationService.readNotification(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
