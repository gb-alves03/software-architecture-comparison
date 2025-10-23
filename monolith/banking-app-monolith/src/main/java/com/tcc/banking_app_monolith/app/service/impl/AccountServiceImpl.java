package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.*;
import com.tcc.banking_app_monolith.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.app.repository.AccountRepository;
import com.tcc.banking_app_monolith.app.repository.CardRepository;
import com.tcc.banking_app_monolith.app.repository.OwnerRepository;
import com.tcc.banking_app_monolith.app.service.AccountService;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.entity.Card;
import com.tcc.banking_app_monolith.domain.entity.Owner;
import com.tcc.banking_app_monolith.domain.enums.CardType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final CardRepository cardRepository;

    private static final SecureRandom random = new SecureRandom();

    public AccountServiceImpl(AccountRepository accountRepository,
                              OwnerRepository ownerRepository,
                              CardRepository cardRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.cardRepository = cardRepository;
    }


    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {
        Owner owner = new Owner();
        owner.setName(dto.name());
        owner.setEmail(dto.email());
        owner.setPhone(dto.phone());
        owner.validate();

        Account account = new Account();
        account.setOwner(owner);
        account.setBalance(BigDecimal.ZERO);
        account.validate();
        account.setCards(new ArrayList<Card>());

        Card card = generateNewCard(account, CardType.CREDIT, new BigDecimal(1000));
        account.getCards().add(card);

        ownerRepository.save(owner);
        accountRepository.save(account);
        this.cardRepository.save(card);

        return new RegisterResponseDto(
                account.getId(),
                account.getOwner().getName(),
                account.getOwner().getEmail(),
                account.getOwner().getPhone(),
                account.getOwner().getId());
    }

    @Override
    public void deposit(DepositRequestDto dto) {

    }

    @Override
    public void withdrawal(WithdrawalRequestDto dto) {

    }

    @Override
    public void transfer(TransferRequestDto dto) {

    }

    @Override
    public void payment(PaymentRequestDto dto) {

    }

    private Card generateNewCard(Account account, CardType type, BigDecimal limit) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            String number = generateCardNumber();
            String cvv = generateCvv();

            if (this.cardRepository.existsCardNumber(number)) {
                continue;
            }

            Card card = new Card();
            card.setNumber(number);
            card.setCvv(cvv);
            card.setType(type);
            card.setAccount(account);
            card.setCreditLimit(limit);
            return card;
        }
        throw new RuntimeException("Error generate unique card number");
    }

    private String generateCardNumber() {
        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        String withoutChecksum = sb.toString();
        int checksum = checkSumDigit(withoutChecksum);
        return withoutChecksum + checksum;
    }

    private String generateCvv() {
        int cvv = random.nextInt(900) + 100;
        return Integer.toString(cvv);
    }

    private int checkSumDigit(String numberWithoutChecksum) {
        int sum = 0;
        boolean doubleDigit = false;

        for (int i = numberWithoutChecksum.length() - 1; i >= 0; i--) {
            int d = Character.getNumericValue(numberWithoutChecksum.charAt(i));
            if (doubleDigit) {
                d = d * 2;
                if (d > 9)
                    d -= 9;
            }
            sum += d;
            doubleDigit = !doubleDigit;
        }
        return (10 - (sum % 10)) % 10;
    }
}
