package com.tcc.banking_app_monolith.account_management.domain.entity;

import com.tcc.banking_app_monolith.account_management.domain.enums.CardType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 16)
    private String number;
    @Column(nullable = false, length = 3)
    private String cvv;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CardType type;
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit;

    public Card() {}

    public Card(Long id, String number, String cvv, CardType type, Account account, BigDecimal creditLimit) {
        this.id = id;
        this.number = number;
        this.cvv = cvv;
        this.type = type;
        this.account = account;
        this.creditLimit = creditLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
}
