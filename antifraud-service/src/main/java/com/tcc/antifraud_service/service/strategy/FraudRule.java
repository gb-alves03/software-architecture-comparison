package com.tcc.antifraud_service.service.strategy;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;

import java.util.Optional;

public interface FraudRule {
    Optional<FraudResponse> apply(FraudRequest request, Long attempts);
}
