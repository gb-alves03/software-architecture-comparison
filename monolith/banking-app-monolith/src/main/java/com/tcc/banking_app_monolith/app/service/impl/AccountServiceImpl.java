package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.*;
import com.tcc.banking_app_monolith.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.app.repository.AccountRepository;
import com.tcc.banking_app_monolith.app.repository.OwnerRepository;
import com.tcc.banking_app_monolith.app.service.AccountService;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.entity.Owner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              OwnerRepository ownerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
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

        ownerRepository.save(owner);
        accountRepository.save(account);

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
}
