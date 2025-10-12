package com.architecture.account_service.events;

import java.math.BigDecimal;

import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.model.Account;

public class PaymentDone {
    private Account account;
    private BigDecimal amount;
    private TransactionType type;

    public PaymentDone(Account account, BigDecimal amount, TransactionType type) {
        this.account = account;
        this.amount = amount;
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
