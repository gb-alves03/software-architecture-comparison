package com.tcc.antifraud_service.service;


import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;

public interface FraudService {
    FraudResponse validate(FraudRequest request);
}
