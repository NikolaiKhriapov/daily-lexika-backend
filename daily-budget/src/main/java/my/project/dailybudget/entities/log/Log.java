package my.project.dailybudget.entities.log;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.dailybudget.enumerations.LogAction;
import my.project.library.util.datetime.DateUtil;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity(name = "logs")
public class Log {

    @Id
    @SequenceGenerator(name = "log_id_sequence", sequenceName = "log_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_id_sequence")
    private Long id;

    private Long userId;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    private LogAction action;

    private OffsetDateTime timestamp;

    private String comment;

    public Log(Long userId, String userEmail, LogAction action) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.action = action;
        this.timestamp = DateUtil.nowInUtc();
    }

    public Log(Long userId, String userEmail, LogAction action, String comment) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.action = action;
        this.timestamp = DateUtil.nowInUtc();
        this.comment = comment;
    }
}
