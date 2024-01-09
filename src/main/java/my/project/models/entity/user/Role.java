package my.project.models.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "roles")
public class Role {

    @Id
    @SequenceGenerator(name = "role_id_sequence", sequenceName = "role_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    public Role(RoleName roleName) {
        this.roleName = roleName;
    }
}
