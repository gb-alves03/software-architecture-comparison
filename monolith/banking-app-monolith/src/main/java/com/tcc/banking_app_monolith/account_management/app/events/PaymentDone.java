package com.tcc.banking_app_monolith.account_management.app.events;

import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;

import java.math.BigDecimal;

public class PaymentDone {

    public static String queue = "payment.done.queue";

    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;

    public PaymentDone(Long accountId, BigDecimal amount, TransactionType type) {
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
