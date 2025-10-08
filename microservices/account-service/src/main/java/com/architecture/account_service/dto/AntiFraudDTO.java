package com.architecture.account_service.dto;

import java.math.BigDecimal;

public abstract class AntiFraudDTO {
    public static record Request(String transactionId, BigDecimal amount, String transactionType, String accountId) {
    };

    public static record Response(String transactionId, boolean fraudulent, String reason) {
    };
}
