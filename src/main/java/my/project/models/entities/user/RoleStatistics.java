package my.project.models.entities.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    private LocalDate dateOfLastStreak;

    private Long recordStreak;

    public RoleStatistics(RoleName roleName) {
        this.roleName = roleName;
        this.currentStreak = 0L;
        this.dateOfLastStreak = LocalDate.now().minusDays(1);
        this.recordStreak = 0L;
    }
}
