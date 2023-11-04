package my.project.services.notification;

import lombok.RequiredArgsConstructor;
import my.project.exception.ResourceNotFoundException;
import my.project.models.dto.notification.NotificationDTO;
import my.project.models.mapper.notification.NotificationMapper;
import my.project.repositories.notification.NotificationRepository;
import my.project.models.entity.notification.Notification;
import my.project.models.dto.user.UserDTO;
import my.project.models.entity.user.User;
import my.project.models.mapper.user.UserMapper;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserMapper userMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final MessageSource messageSource;

    private UserDTO getAuthenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    public List<NotificationDTO> getAllNotifications() {
        Long userId = getAuthenticatedUser().id();
        List<Notification> listOfNotifications = notificationRepository.findAllByToUserId(userId);
        return notificationMapper.toDTOList(listOfNotifications);
    }

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void readNotification(Long notificationId) {
        Long userId = getAuthenticatedUser().id();
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
