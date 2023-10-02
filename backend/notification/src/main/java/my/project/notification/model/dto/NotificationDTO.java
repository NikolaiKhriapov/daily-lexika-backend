package my.project.notification.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record NotificationDTO(

        @Nullable
        Long notificationId,
        Long toUserId,
        String toUserEmail,
        String sender,
        String subject,
        String message,
        LocalDateTime sentAt,
        Boolean isRead
) {
}