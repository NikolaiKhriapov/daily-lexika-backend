package my.project.dailylexika.entities.user;

import jakarta.persistence.*;
import lombok.*;
import my.project.library.util.datetime.DateUtil;
import my.project.library.dailylexika.enumerations.RoleName;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "role_statistics")
public class RoleStatistics {

    @Id
    @SequenceGenerator(name = "role_statistics_id_sequence", sequenceName = "role_statistics_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_statistics_id_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    private Long currentStreak;

    private OffsetDateTime dateOfLastStreak;

    private Long recordStreak;

    public RoleStatistics(RoleName roleName) {
        this.roleName = roleName;
        this.currentStreak = 0L;
        this.dateOfLastStreak = DateUtil.nowInUtc().minusDays(1);
        this.recordStreak = 0L;
    }
}
