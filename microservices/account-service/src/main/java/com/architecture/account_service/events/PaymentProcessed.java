package com.architecture.account_service.events;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.model.Account;

public class PaymentProcessed {
    
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;

    public PaymentProcessed(Long accountId, BigDecimal amount, TransactionType type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
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
