package com.tcc.banking_app_monolith.app.event;

import com.tcc.banking_app_monolith.domain.enums.PaymentStatus;
import com.tcc.banking_app_monolith.domain.enums.PaymentType;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;

import java.math.BigDecimal;

public class PaymentProcessed {

    private Long transactionId;
    private Long accountId;
    private BigDecimal amount;
    private PaymentType type;
    private PaymentStatus status;

    public PaymentProcessed(Long transactionId, Long accountId, BigDecimal amount, PaymentType type, PaymentStatus status) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
