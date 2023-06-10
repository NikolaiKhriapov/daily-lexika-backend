package my.project.fraud_check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudCheckHistoryService {

    private final FraudCheckHistoryRepository fraudCheckHistoryRepository;

    public boolean isFraudster(Long applicationUserId) {
        FraudCheckHistory fraudCheckHistory = new FraudCheckHistory();

        fraudCheckHistory.setApplicationUserId(applicationUserId);
        fraudCheckHistory.setIsFraudster(false);
        fraudCheckHistory.setCreatedAt(LocalDateTime.now());

        fraudCheckHistoryRepository.save(fraudCheckHistory);

        return false;
    }
}
