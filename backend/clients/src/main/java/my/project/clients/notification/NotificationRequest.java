package my.project.clients.notification;

public record NotificationRequest(

        Long toUserId,
        String toUserEmail,
        String message
) {
}
