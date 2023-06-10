package my.project.fraud_check;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fraud-check")
@RequiredArgsConstructor
public class FraudCheckHistoryController {

    private final FraudCheckHistoryService fraudCheckHistoryService;

    @GetMapping("/{applicationUserId}")
    public FraudCheckResponse isFraudster(@PathVariable("applicationUserId") Long applicationUserId) {
        boolean isFraudster = fraudCheckHistoryService.isFraudster(applicationUserId);
        return new FraudCheckResponse(isFraudster);
    }
}

