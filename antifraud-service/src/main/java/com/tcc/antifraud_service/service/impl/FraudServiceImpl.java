package com.tcc.antifraud_service.service.impl;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import com.tcc.antifraud_service.repository.FraudRepository;
import com.tcc.antifraud_service.repository.mongo.FraudCheckDocument;
import com.tcc.antifraud_service.service.FraudService;
import com.tcc.antifraud_service.service.strategy.FraudRule;
import com.tcc.antifraud_service.service.strategy.TransferOverLimitRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FraudServiceImpl implements FraudService {

    private static final Logger log = LoggerFactory.getLogger(FraudServiceImpl.class);

    private final FraudRepository repository;
    private final StringRedisTemplate redisTemplate;

    public FraudServiceImpl(FraudRepository repository,
                            StringRedisTemplate redisTemplate,
                            MeterRegistry meterRegistry,
                            List<FraudRule> fraudRules) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public FraudResponse validate(FraudRequest request) {
        String key = "txn:" + request.accountId();
        Long attempts;

        try {
            attempts = redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, Duration.ofMinutes(2));
        } catch (Exception e) {
            log.warn("Redis unavailable defaulting attempts to 1", e);
            attempts = 1L;
        }

        FraudResponse response = null;

        TransferOverLimitRule rule = new TransferOverLimitRule();

        Optional<FraudResponse> maybe = rule.apply(request, attempts);
        if (maybe.isPresent()) {
            response = maybe.get();
        }

        if (response == null) {
            response = new FraudResponse(
                    request.transactionId(),
                    false,
                    "Transação autorizada!"
            );
        }

        repository.save(new FraudCheckDocument(
                UUID.randomUUID().toString(),
                request.accountId(),
                request.amount(),
                request.transactionType(),
                response.fraudulent(),
                response.reason()
        ));

        return response;
    }
}
