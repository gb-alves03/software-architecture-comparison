package com.tcc.antifraud_service.service.impl;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import com.tcc.antifraud_service.repository.FraudRepository;
import com.tcc.antifraud_service.repository.mongo.FraudCheckDocument;
import com.tcc.antifraud_service.service.FraudService;
import com.tcc.antifraud_service.service.strategy.FraudRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FraudServiceImpl implements FraudService {

    private final FraudRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final Counter approvedCounter;
    private final Counter rejectedCounter;
    private final List<FraudRule> fraudRules;

    public FraudServiceImpl(FraudRepository repository,
                            StringRedisTemplate redisTemplate,
                            MeterRegistry meterRegistry,
                            List<FraudRule> fraudRules) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.approvedCounter = meterRegistry.counter("fraud.approved");
        this.rejectedCounter = meterRegistry.counter("fraud.rejected");
        this.fraudRules = fraudRules;
    }

    @Override
    public FraudResponse validate(FraudRequest request) {
        String key = "txn:" + request.accountId();
        Long attempts;

        try {
            attempts = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofMinutes(2));
        } catch (Exception e) {
            attempts = 1L;
        }


        Long finalAttempts = attempts;
        FraudResponse response = fraudRules.stream()
                        .map(rule -> rule.apply(request, finalAttempts))
                                .filter(Optional::isPresent)
                                        .map(Optional::get)
                                                .findFirst()
                                                        .orElse(new FraudResponse(
                                                                request.transactionId(),
                                                                false,
                                                                "Transação autorizada!"
                                                        ));

        repository.save(new FraudCheckDocument(
                UUID.randomUUID().toString(),
                request.accountId(),
                request.amount(),
                request.transactionType(),
                response.fraudulent(),
                response.reason()
        ));

        if (response.fraudulent()) {
            rejectedCounter.increment();
        }
        approvedCounter.increment();

        return response;
    }

}
