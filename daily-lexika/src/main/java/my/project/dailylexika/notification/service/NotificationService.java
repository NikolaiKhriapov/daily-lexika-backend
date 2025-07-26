package my.project.dailylexika.notification.service;

import my.project.dailylexika.notification.model.entities.Notification;
import my.project.library.dailylexika.dtos.notification.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getAll();
    void sendNotification(Notification notification);
    void readNotification(Integer notificationId);
    void deleteAllByUserId(Integer userId);
}
