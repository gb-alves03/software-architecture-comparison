package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.AccountPaymentRequestDto;
import com.tcc.banking_app_monolith.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.entity.Card;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;

import java.util.List;

public class CreditPaymentProcessor implements PaymentProcessor {

    @Override
    public TransactionType getType() {
        return TransactionType.CREDIT;
    }

    @Override
    public void process(Account account, AccountPaymentRequestDto dto) {
        List<Card> cards = account.getCards();
        for (Card card : cards) {
            if (card.getCreditLimit().compareTo(dto.amount()) < 0) {
                throw new RuntimeException("Insufficient credit card limit for credit transaction");
            }
            card.setCreditLimit(card.getCreditLimit().subtract(dto.amount()));
            return;
        }
        throw new RuntimeException("No card with sufficient credit limit");
    }
}
