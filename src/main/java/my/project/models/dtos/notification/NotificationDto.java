package my.project.models.dtos.notification;

import my.project.models.entities.notification.Notification;

import java.io.Serializable;
import java.time.OffsetDateTime;

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
        OffsetDateTime sentAt,
        Boolean isRead

) implements Serializable {
}
