package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.AccountPaymentRequestDto;
import com.tcc.banking_app_monolith.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class DebitPaymentProcessor implements PaymentProcessor {

    @Override
    public TransactionType getType() {
        return TransactionType.DEBIT;
    }

    @Override
    public void process(Account account, AccountPaymentRequestDto dto) {
        if (account.getBalance().compareTo(dto.amount()) < 0) {
            throw new RuntimeException("Insufficient balance for debit transaction");
        }
        account.setBalance(account.getBalance().subtract(dto.amount()));
    }
}
