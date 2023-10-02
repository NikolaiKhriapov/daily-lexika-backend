package my.project.notification.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "notifications")
public class Notification {

    @Id
    @SequenceGenerator(name = "notification_id_sequence", sequenceName = "notification_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_sequence")
    private Long notificationId;
    private Long toUserId;
    private String toUserEmail;
    private String sender;
    private String subject;
    private String message;
    private LocalDateTime sentAt;
    private Boolean isRead;

    public Notification(Long toUserId, String toUserEmail, String sender, String subject, String message) {
        this.toUserId = toUserId;
        this.toUserEmail = toUserEmail;
        this.sender = sender;
        this.subject = subject;
        this.message = message;
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }
}
