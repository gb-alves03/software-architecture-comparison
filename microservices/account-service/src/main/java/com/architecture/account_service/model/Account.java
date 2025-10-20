package com.architecture.account_service.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "ownerId")
    private Owner owner;
    @OneToMany(mappedBy = "account")
    private List<Card> cards;

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

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
