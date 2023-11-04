package my.project.models.mapper.notification;

import my.project.models.dto.notification.NotificationDTO;
import my.project.models.entity.notification.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper implements Mapper<Notification, NotificationDTO> {

    @Override
    public NotificationDTO toDTO(Notification entity) {
        return new NotificationDTO(
                entity.getNotificationId(),
                entity.getToUserId(),
                entity.getToUserEmail(),
                entity.getSender(),
                entity.getSubject(),
                entity.getMessage(),
                entity.getSentAt(),
                entity.getIsRead()
        );
    }
}
