package my.project.notification.model.mapper;

import my.project.clients.notification.NotificationRequest;
import my.project.notification.model.dto.NotificationDTO;
import my.project.notification.model.entity.Notification;
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

    public Notification toEntity(NotificationRequest request) {
        return new Notification(
                request.toUserId(),
                request.toUserEmail(),
                "Chinese Learning App",
                request.subject(),
                request.message()
        );
    }
}
