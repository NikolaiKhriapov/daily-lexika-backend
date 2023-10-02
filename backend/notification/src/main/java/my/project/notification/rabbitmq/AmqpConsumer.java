package my.project.notification.rabbitmq;

import lombok.AllArgsConstructor;
import my.project.clients.notification.NotificationRequest;
import my.project.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AmqpConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queues.send-welcome-notification}")
    public void consumer(NotificationRequest notificationRequest) {
        notificationService.sendNotification(notificationRequest);
    }
}
