package com.architecture.account_service.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "ownerId")
    private Owner owner;

    public void validate() {
        if (owner == null) {
            throw new RuntimeException("Owner is required");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Invalid balance");
        }
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
