package my.project.fraudcheck;

import lombok.RequiredArgsConstructor;
import my.project.clients.fraudcheck.FraudCheckResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fraud-check")
@RequiredArgsConstructor
public class FraudCheckHistoryController {

    private final FraudCheckHistoryService fraudCheckHistoryService;

    @GetMapping("/{userId}")
    public FraudCheckResponse isFraudster(@PathVariable("userId") Long userId) {
        boolean isFraudster = fraudCheckHistoryService.isFraudster(userId);
        return new FraudCheckResponse(isFraudster);
    }
}
