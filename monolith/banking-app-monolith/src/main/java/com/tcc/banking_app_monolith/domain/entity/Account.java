package com.tcc.banking_app_monolith.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Owner owner;
    @OneToMany(mappedBy = "account")
    private List<Card> cards;


    public Account() {}

    public Account(Long id, BigDecimal balance, Owner owner, List<Card> cards) {
        this.id = id;
        this.balance = balance;
        this.owner = owner;
        this.cards = cards;
    }

    public void validate() {
        if (owner == null) {
            throw new RuntimeException("Owner is required");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Invalid balance");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
