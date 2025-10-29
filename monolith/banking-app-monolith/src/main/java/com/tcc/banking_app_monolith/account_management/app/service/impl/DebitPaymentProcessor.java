package com.tcc.banking_app_monolith.account_management.app.service.impl;

import com.tcc.banking_app_monolith.account_management.app.dto.request.PaymentRequestDto;
import com.tcc.banking_app_monolith.account_management.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.account_management.domain.entity.Account;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class DebitPaymentProcessor implements PaymentProcessor {

    @Override
    public TransactionType getType() {
        return TransactionType.DEBIT;
    }

    @Override
    public void process(Account account, PaymentRequestDto dto) {
        if (account.getBalance().compareTo(dto.amount()) < 0) {
            throw new RuntimeException("Insufficient balance for debit transaction");
        }
        account.setBalance(account.getBalance().subtract(dto.amount()));
    }
}
