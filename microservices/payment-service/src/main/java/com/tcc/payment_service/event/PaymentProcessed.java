package com.tcc.payment_service.event;

import com.tcc.payment_service.enumeration.PaymentStatus;

public class PaymentProcessed {
    private Long transactionId;
    private PaymentStatus status;

    public PaymentProcessed(Long transactionId, PaymentStatus status) {
        this.transactionId = transactionId;
        this.status = status;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

}
