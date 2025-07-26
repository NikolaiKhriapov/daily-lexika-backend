package my.project.dailylexika.notification.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.util.datetime.DateUtil;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "notifications")
public class Notification {

    @Id
    @SequenceGenerator(name = "notification_id_sequence", sequenceName = "notification_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_sequence")
    private Integer notificationId;
    private Integer toUserId;
    private String toUserEmail;
    private String sender;
    private String subject;
    private String message;
    private OffsetDateTime sentAt;
    private Boolean isRead;

    public Notification(Integer toUserId, String toUserEmail, String subject, String message) {
        this.toUserId = toUserId;
        this.toUserEmail = toUserEmail;
        this.sender = "Daily Lexika";
        this.subject = subject;
        this.message = message;
        this.sentAt = DateUtil.nowInUtc();
        this.isRead = false;
    }
}
