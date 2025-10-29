package com.tcc.banking_app_monolith.account_management.domain.entity;

import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionStatus;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;
import jakarta.persistence.*;


import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;
    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id", referencedColumnName = "id")
    private Account from;
    @ManyToOne(optional = false)
    @JoinColumn(name = "to_account_id", referencedColumnName = "id")
    private Account to;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public Transaction() {}

    public Transaction(Long id, TransactionType type, Account from, Account to, TransactionStatus status, BigDecimal amount) {
        this.id = id;
        this.type = type;
        this.from = from;
        this.to = to;
        this.status = status;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
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

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
