package com.architecture.account_service.service;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.architecture.account_service.dto.CreditDTO;
import com.architecture.account_service.dto.DebitDTO;
import com.architecture.account_service.dto.ProfileDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.model.Account;
import com.architecture.account_service.model.Owner;
import com.architecture.account_service.repository.AccountRepository;
import com.architecture.account_service.repository.OwnerRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;

    public AccountService(AccountRepository accountRepository, OwnerRepository ownerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
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

    @Transactional
    public void debit(DebitDTO.Input input) {
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        BigDecimal amount = input.amount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greather than zero");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        this.accountRepository.save(account);
    }

    @Transactional
    public void credit(CreditDTO.Input input) {
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        BigDecimal amount = input.amount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greather than zero");
        }
        account.setBalance(account.getBalance().add(amount));
        this.accountRepository.save(account);
    }

    public ProfileDTO.Output findAccountById(ProfileDTO.Input input) {
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
        Long loggedAccountId = accountDetails.getAccountId();

        if (!account.getAccountId().equals(loggedAccountId)) {
            throw new RuntimeException("Access denied");
        }

        return new ProfileDTO.Output(account.getAccountId(), account.getBalance(), account.getOwner().getName(),
                account.getOwner().getEmail(), account.getOwner().getPhone());
    }
}
