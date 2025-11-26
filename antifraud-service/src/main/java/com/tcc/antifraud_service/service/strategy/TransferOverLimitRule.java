package com.tcc.antifraud_service.service.strategy;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransferOverLimitRule implements FraudRule {

    @Override
    public Optional<FraudResponse> apply(FraudRequest request, Long attempts) {
        if (request.amount().doubleValue() > 10000) {
            return Optional.of(new FraudResponse(
                    request.transactionId(),
                    true,
                    "Transação acima do limite permitido."
            ));
        }
        return Optional.empty();
    }
}
