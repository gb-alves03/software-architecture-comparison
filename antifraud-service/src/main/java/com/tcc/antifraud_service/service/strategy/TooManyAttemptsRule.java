package com.tcc.antifraud_service.service.strategy;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TooManyAttemptsRule implements FraudRule {

    @Override
    public Optional<FraudResponse> apply(FraudRequest request, Long attempts) {
        if (attempts != null && attempts > 3) {
            return Optional.of(new FraudResponse(
                    request.transactionId(),
                    true,
                    "Muitas tentativas em pouco tempo"
            ));
        }
        return Optional.empty();
    }
}

