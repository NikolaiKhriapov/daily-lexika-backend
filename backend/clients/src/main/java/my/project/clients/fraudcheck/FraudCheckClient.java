package my.project.clients.fraudcheck;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("fraud-check")
public interface FraudCheckClient {

    @GetMapping("api/v1/fraud-check/{userId}")
    FraudCheckResponse isFraudster(@PathVariable("userId") Long userId);
}
