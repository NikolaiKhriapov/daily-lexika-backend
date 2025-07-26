package my.project.dailylexika.notification.service.impl;

import lombok.RequiredArgsConstructor;
import my.project.dailylexika.config.I18nUtil;
import my.project.dailylexika.notification.service.NotificationService;
import my.project.dailylexika.user._public.PublicUserService;
import my.project.library.dailylexika.dtos.notification.NotificationDto;
import my.project.dailylexika.notification.model.mappers.NotificationMapper;
import my.project.dailylexika.notification.persistence.NotificationRepository;
import my.project.dailylexika.notification.model.entities.Notification;
import my.project.library.util.exception.InternalServerErrorException;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;
    private final PublicUserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getAll() {
        Integer userId = userService.getUser().id();
        List<Notification> listOfNotifications = notificationRepository.findAllByToUserId(userId);
        return notificationMapper.toDtoList(listOfNotifications);
    }

    @Override
    @Transactional
    public void sendNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void readNotification(Integer notificationId) {
        Integer userId = userService.getUser().id();
        Notification notificationToBeUpdated = getNotificationById(notificationId);

        verifyNotificationIsForThisUser(notificationToBeUpdated, userId);

        notificationToBeUpdated.setIsRead(true);

        notificationRepository.save(notificationToBeUpdated);
    }

    @Override
    @Transactional
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
