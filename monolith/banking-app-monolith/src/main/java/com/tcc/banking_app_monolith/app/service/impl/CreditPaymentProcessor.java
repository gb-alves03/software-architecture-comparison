package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.AccountPaymentRequestDto;
import com.tcc.banking_app_monolith.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.entity.Card;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;
import com.tcc.banking_app_monolith.utils.Constants;
import org.springframework.stereotype.Component;

@Component
public class CreditPaymentProcessor implements PaymentProcessor {

    @Override
    public TransactionType getType() {
        return TransactionType.CREDIT;
    }

    @Override
    public void process(Account account, AccountPaymentRequestDto dto) {
        Card card = account.getCard();

        if (card.getCreditLimit().compareTo(dto.amount()) < 0) {
            throw new RuntimeException(Constants.INSUFFICIENT_LIMIT.getValue());
        }
        card.setCreditLimit(card.getCreditLimit().subtract(dto.amount()));
    }
}
