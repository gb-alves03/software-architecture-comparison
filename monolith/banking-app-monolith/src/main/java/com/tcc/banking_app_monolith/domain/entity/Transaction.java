package com.tcc.banking_app_monolith.domain.entity;

import com.tcc.banking_app_monolith.domain.enums.TransactionStatus;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;
import jakarta.persistence.*;


import java.math.BigDecimal;

@Entity
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
