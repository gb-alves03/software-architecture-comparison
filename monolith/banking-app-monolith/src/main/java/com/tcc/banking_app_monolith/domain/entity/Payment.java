package com.tcc.banking_app_monolith.domain.entity;

import com.tcc.banking_app_monolith.domain.enums.PaymentStatus;
import com.tcc.banking_app_monolith.domain.enums.PaymentType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "accountId", nullable = false)
    private Long accountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "pstatus", nullable = false)
    private PaymentStatus status;
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "type", nullable = false)
    private PaymentType paymentType;

    public Payment() {}

    public Payment(Long id, Long accountId, PaymentStatus status, BigDecimal amount, PaymentType paymentType) {
        this.id = id;
        this.accountId = accountId;
        this.status = status;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
}
