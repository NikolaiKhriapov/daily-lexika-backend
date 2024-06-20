package my.project.dailylexika.entities.log;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.util.datetime.DateUtil;
import my.project.library.dailylexika.enumerations.LogAction;
import my.project.library.dailylexika.enumerations.Platform;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity(name = "logs")
public class Log {

    @Id
    @SequenceGenerator(name = "log_id_sequence", sequenceName = "log_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_id_sequence")
    private Long id;

    private Integer userId;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    private LogAction action;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private OffsetDateTime timestamp;

    private String comment;

    public Log(Integer userId, String userEmail, LogAction action, Platform platform) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.action = action;
        this.platform = platform;
        this.timestamp = DateUtil.nowInUtc();
    }

    public Log(Integer userId, String userEmail, LogAction action, Platform platform, String comment) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.action = action;
        this.platform = platform;
        this.timestamp = DateUtil.nowInUtc();
        this.comment = comment;
    }
}
