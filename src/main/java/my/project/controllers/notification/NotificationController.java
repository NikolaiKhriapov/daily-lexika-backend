package my.project.controllers.notification;

import lombok.AllArgsConstructor;
import my.project.models.dto.notification.NotificationDTO;
import my.project.services.notification.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @PatchMapping("/read/{notificationId}")
    public ResponseEntity<Void> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
