package my.project.notification;

import lombok.RequiredArgsConstructor;
import my.project.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void send(NotificationRequest notificationRequest) {
        notificationRepository.save(
                new Notification(
                        notificationRequest.toUserId(),
                        notificationRequest.toUserEmail(),
                        "Chinese Learning App",
                        notificationRequest.message(),
                        LocalDateTime.now()
                )
        );
    }
}
