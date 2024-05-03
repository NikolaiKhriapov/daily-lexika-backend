package my.project.controllers.notification;

import lombok.AllArgsConstructor;
import my.project.models.dtos.notification.NotificationDto;
import my.project.services.notification.NotificationService;
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
