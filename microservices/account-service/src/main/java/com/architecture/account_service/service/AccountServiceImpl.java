package com.architecture.account_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;
import com.architecture.account_service.model.Account;
import com.architecture.account_service.model.Owner;
import com.architecture.account_service.repository.AccountRepository;
import com.architecture.account_service.repository.OwnerRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;

    public AccountServiceImpl(AccountRepository accountRepository, OwnerRepository ownerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
    }

    @Override
    public void deposit(DepositDTO.Input input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void payment(PaymentDTO.Input input) {
        // TODO Auto-generated method stub

    }

    @Transactional
    @Override
    public void register(RegisterDTO.Input input) {
        Owner owner = new Owner();
        owner.setName(input.name());
        owner.setEmail(input.email());
        owner.setPhone(input.phone());
        owner.validate();

        Account account = new Account();
        account.setOwner(owner);
        account.setBalance(BigDecimal.ZERO);
        account.validate();

        ownerRepository.save(owner);
        accountRepository.save(account);
    }

    @Override
    public void transfer(TransferDTO.Input input) {
        // TODO Auto-generated method stub

    }

    @Override
    public void withdrawal(WithdrawalDTO.Input input) {
        // TODO Auto-generated method stub

    }

}
