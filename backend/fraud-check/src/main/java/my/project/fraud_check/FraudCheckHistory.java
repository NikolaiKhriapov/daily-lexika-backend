package my.project.fraud_check;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudCheckHistory {

    @Id
    @SequenceGenerator(name = "fraud_check_id_sequence", sequenceName = "fraud_check_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fraud_check_id_sequence")
    private Long id;
    private Long applicationUserId;
    private Boolean isFraudster;
    private LocalDateTime createdAt;
}
