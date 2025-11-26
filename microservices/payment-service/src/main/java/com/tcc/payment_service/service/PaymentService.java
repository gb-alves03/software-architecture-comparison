package com.tcc.payment_service.service;

import com.tcc.payment_service.dto.PaymentDTO;

public interface PaymentService {
    void processPayment(PaymentDTO.Input input);
}
