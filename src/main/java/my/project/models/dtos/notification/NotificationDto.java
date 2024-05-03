package my.project.models.dtos.notification;

import my.project.models.entities.notification.Notification;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Notification}
 */
public record NotificationDto(

        Integer notificationId,
        Integer toUserId,
        String toUserEmail,
        String sender,
        String subject,
        String message,
        LocalDateTime sentAt,
        Boolean isRead

) implements Serializable {
}
