package com.architecture.account_service.events;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.model.Account;

public class PaymentDone {
    private static final String event = "paymentDone";
    private Account from = null;
    private Account to = null;
    private BigDecimal amount;
    private TransactionType type;

    public PaymentDone(Account from, Account to, BigDecimal amount, TransactionType type) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.type = type;
    }

    public Account getFrom() {
        return from;
    }

    public void setFrom(Account from) {
        this.from = from;
    }

    public Account getTo() {
        return to;
    }

    public void setTo(Account to) {
        this.to = to;
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
