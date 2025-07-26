package my.project.dailylexika.notification.service;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.i18n.I18nUtil;
import my.project.library.dailylexika.dtos.notification.NotificationDto;
import my.project.dailylexika.notification.model.mappers.NotificationMapper;
import my.project.dailylexika.notification.persistence.NotificationRepository;
import my.project.dailylexika.notification.model.entities.Notification;
import my.project.library.dailylexika.dtos.user.UserDto;
import my.project.dailylexika.user.model.entities.User;
import my.project.dailylexika.user.model.mappers.UserMapper;
import my.project.library.util.exception.InternalServerErrorException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserMapper userMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private UserDto getAuthenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toDTO(user);
    }

    public List<NotificationDto> getAllNotifications() {
        Integer userId = getAuthenticatedUser().id();
        List<Notification> listOfNotifications = notificationRepository.findAllByToUserId(userId);
        return notificationMapper.toDtoList(listOfNotifications);
    }

    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void readNotification(Integer notificationId) {
        Integer userId = getAuthenticatedUser().id();
        Notification notificationToBeUpdated = getNotificationById(notificationId);

        verifyNotificationIsForThisUser(notificationToBeUpdated, userId);

        notificationToBeUpdated.setIsRead(true);

        notificationRepository.save(notificationToBeUpdated);
    }

    public void deleteAllByUserId(Integer userId) {
        notificationRepository.deleteAllByToUserId(userId);
    }

    private Notification getNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailylexika-exceptions.notification.notFound")));
    }

    private void verifyNotificationIsForThisUser(Notification notification, Integer userId) {
        if (!Objects.equals(notification.getToUserId(), userId)) {
            throw new InternalServerErrorException(I18nUtil.getMessage("dailylexika-exceptions.notification.invalidUser"));
        }
    }
}
