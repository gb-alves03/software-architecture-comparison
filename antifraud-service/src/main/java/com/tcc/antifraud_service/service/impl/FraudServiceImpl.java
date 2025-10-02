package com.tcc.antifraud_service.service.impl;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import com.tcc.antifraud_service.repository.FraudRepository;
import com.tcc.antifraud_service.repository.mongo.FraudCheckDocument;
import com.tcc.antifraud_service.service.FraudService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class FraudServiceImpl implements FraudService {

    private final FraudRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final Counter approvedCounter;
    private final Counter rejectedCounter;

    public FraudServiceImpl(FraudRepository repository,
                            StringRedisTemplate redisTemplate,
                            MeterRegistry meterRegistry) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.approvedCounter = meterRegistry.counter("fraud.approved");
        this.rejectedCounter = meterRegistry.counter("fraud.rejected");

    }

    @Override
    public FraudResponse validate(FraudRequest request) {
        String key = "txn:" + request.sourceAccount();
        final var defaultResponse = new FraudResponse(request.transactionId(), false, "Transação autorizada!");

        Long attempts;
        try {
            attempts = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofMinutes(2));
        } catch (Exception e) {
            attempts = 1L;
        }


        FraudResponse response = getFraudResponse(request, attempts, defaultResponse);

        repository.save(new FraudCheckDocument(
                UUID.randomUUID().toString(),
                request.sourceAccount(),
                request.amount(),
                request.transactionType(),
                response.fraudulent()
        ));

        if (response.fraudulent()) {
            rejectedCounter.increment();
        }
        approvedCounter.increment();

        return response;
    }

    private static FraudResponse getFraudResponse(FraudRequest request, Long attempts, FraudResponse defaultResponse) {
        FraudResponse response;

        if (attempts != null && attempts > 3) {
            response =  new FraudResponse(request.transactionId(), true, "Muitas tentativas em pouco tempo");
        } else if (request.amount().doubleValue() > 10000) {
            response = new FraudResponse(request.transactionId(), true, "Transação acima do limite permitido.");
        } else if ("TRANSFER".equals(request.transactionType()) &&
        request.sourceAccount().equals(request.destinationAccount())) {
            response =  new FraudResponse(request.transactionId(), true, "Transferência entre mesma conta!");
        } else {
            response = defaultResponse;
        }
        return response;
    }
}
