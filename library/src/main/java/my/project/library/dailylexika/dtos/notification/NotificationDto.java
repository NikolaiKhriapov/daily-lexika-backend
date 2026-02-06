package my.project.library.dailylexika.dtos.notification;

import java.time.OffsetDateTime;

public record NotificationDto(

        Integer notificationId,
        Integer toUserId,
        String toUserEmail,
        String sender,
        String subject,
        String message,
        OffsetDateTime sentAt,
        Boolean isRead

) {
}
