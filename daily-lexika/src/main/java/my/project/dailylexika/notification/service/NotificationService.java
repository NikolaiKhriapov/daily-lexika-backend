package my.project.dailylexika.notification.service;

import my.project.dailylexika.notification.model.entities.Notification;
import my.project.dailylexika.user.model.entities.RoleStatistics;
import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.notification.NotificationDto;
import my.project.library.dailylexika.dtos.user.RoleStatisticsDto;
import my.project.library.dailylexika.enumerations.Platform;
import my.project.library.dailylexika.enumerations.RoleName;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getAllNotifications();
    void sendNotification(Notification notification);
    void readNotification(Integer notificationId);
    void deleteAllByUserId(Integer userId);
}
