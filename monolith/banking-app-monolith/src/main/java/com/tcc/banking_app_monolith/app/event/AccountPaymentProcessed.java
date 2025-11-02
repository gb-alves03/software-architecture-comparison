package com.tcc.banking_app_monolith.app.event;

import com.tcc.banking_app_monolith.domain.enums.PaymentStatus;
import com.tcc.banking_app_monolith.domain.enums.PaymentType;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;

import java.math.BigDecimal;

public class AccountPaymentProcessed {

    private Long transactionId;
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;

    public AccountPaymentProcessed(Long transactionId, Long accountId, BigDecimal amount, TransactionType type) {
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
