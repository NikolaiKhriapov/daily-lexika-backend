package my.project.fraudcheck;

import lombok.RequiredArgsConstructor;
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
