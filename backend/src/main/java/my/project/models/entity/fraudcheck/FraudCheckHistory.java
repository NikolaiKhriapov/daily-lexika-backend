package my.project.models.entity.fraudcheck;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "fraud_check_history")
public class FraudCheckHistory {

    @Id
    @SequenceGenerator(name = "fraud_check_id_sequence", sequenceName = "fraud_check_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fraud_check_id_sequence")
    private Long id;
    private Long userId;
    private Boolean isFraudster;
    private LocalDateTime createdAt;
}
