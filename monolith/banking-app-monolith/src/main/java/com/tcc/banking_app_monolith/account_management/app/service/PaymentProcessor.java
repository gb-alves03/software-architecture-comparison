package com.tcc.banking_app_monolith.account_management.app.service;

import com.tcc.banking_app_monolith.account_management.app.dto.request.PaymentRequestDto;
import com.tcc.banking_app_monolith.account_management.domain.entity.Account;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;

public interface PaymentProcessor {
    TransactionType getType();
    void process(Account account, PaymentRequestDto dto);
}
