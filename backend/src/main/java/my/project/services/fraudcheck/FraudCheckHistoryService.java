package my.project.services.fraudcheck;

import lombok.RequiredArgsConstructor;
import my.project.models.entity.fraudcheck.FraudCheckHistory;
import my.project.repositories.fraudcheck.FraudCheckHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudCheckHistoryService {

    private final FraudCheckHistoryRepository fraudCheckHistoryRepository;

    public boolean isFraudster(Long userId) {
        FraudCheckHistory fraudCheckHistory = new FraudCheckHistory();

        fraudCheckHistory.setUserId(userId);
        fraudCheckHistory.setIsFraudster(false);
        fraudCheckHistory.setCreatedAt(LocalDateTime.now());

        fraudCheckHistoryRepository.save(fraudCheckHistory);

        return false;
    }
}
