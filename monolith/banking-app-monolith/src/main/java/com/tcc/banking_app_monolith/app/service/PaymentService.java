package com.tcc.banking_app_monolith.app.service;

import com.tcc.banking_app_monolith.app.dto.request.PaymentRequestDto;

public interface PaymentService {

    void processPayment(PaymentRequestDto dto);
}
