package my.project.notification.service;

import lombok.RequiredArgsConstructor;
import my.project.clients.notification.NotificationRequest;
import my.project.notification.exception.ResourceNotFoundException;
import my.project.notification.model.dto.NotificationDTO;
import my.project.notification.model.mapper.NotificationMapper;
import my.project.notification.repository.NotificationRepository;
import my.project.notification.model.entity.Notification;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final MessageSource messageSource;

    public List<NotificationDTO> getAllNotifications(Long userId) {
        List<Notification> listOfNotifications = notificationRepository.findAllByToUserId(userId);
        return notificationMapper.toDTOList(listOfNotifications);
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        Notification notification = notificationMapper.toEntity(notificationRequest);
        notificationRepository.save(notification);
    }

    public void readNotification(Long userId, Long notificationId) {
        Notification notificationToBeUpdated = getNotificationById(notificationId);

        verifyNotificationIsForThisUser(notificationToBeUpdated, userId);

        notificationToBeUpdated.setIsRead(true);

        notificationRepository.save(notificationToBeUpdated);
    }

    private Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage(
                        "exception.notification.notFound", null, Locale.getDefault())));
    }

    private void verifyNotificationIsForThisUser(Notification notification, Long userId) {
        if (!Objects.equals(notification.getToUserId(), userId)) {
            throw new RuntimeException(messageSource.getMessage(
                    "exception.notification.invalidUser", null, Locale.getDefault()));
        }
    }
}
