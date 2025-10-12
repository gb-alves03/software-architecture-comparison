package com.tcc.payment_service.dto;

public abstract class PaymentDTO {
    public static record Input(String accountId);
}
