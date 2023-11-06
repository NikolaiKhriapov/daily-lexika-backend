package my.project.repositories.fraudcheck;

import my.project.models.entity.fraudcheck.FraudCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudCheckHistoryRepository extends JpaRepository<FraudCheckHistory, Long> {
}
