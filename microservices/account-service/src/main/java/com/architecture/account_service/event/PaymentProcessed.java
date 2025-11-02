package com.architecture.account_service.event;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionType;

public class PaymentProcessed {

    private Long transactionId;
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;

    public PaymentProcessed(Long transactionId, Long accountId, BigDecimal amount, TransactionType type) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

}
