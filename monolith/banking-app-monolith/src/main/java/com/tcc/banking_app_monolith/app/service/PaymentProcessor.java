package com.tcc.banking_app_monolith.app.service;

import com.tcc.banking_app_monolith.app.dto.request.AccountPaymentRequestDto;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;

public interface PaymentProcessor {
    TransactionType getType();
    void process(Account account, AccountPaymentRequestDto dto);
}
