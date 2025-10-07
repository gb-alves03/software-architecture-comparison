package com.architecture.account_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;
import com.architecture.account_service.enumeration.TransactionStatus;
import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.model.Account;
import com.architecture.account_service.model.Owner;
import com.architecture.account_service.model.Transaction;
import com.architecture.account_service.repository.AccountRepository;
import com.architecture.account_service.repository.OwnerRepository;
import com.architecture.account_service.repository.TransactionRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository, OwnerRepository ownerRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public void deposit(DepositDTO.Input input) {
        if (input == null || input.amount() == null || input.accountId() == null) {
            throw new RuntimeException("Deposit input, amount, and accountId cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(input.amount());
        
        this.transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().add(input.amount()));
            this.accountRepository.save(account);
            
            transaction.setStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);
            throw new RuntimeException(e.getMessage());
        }
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

    @Transactional
    @Override
    public void transfer(TransferDTO.Input input) {
        if (input == null || input.from() == null || input.to() == null || input.amount() == null) {
            throw new RuntimeException("Transper input, from, to and amount cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }
        Account from = this.accountRepository.findById(input.from())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account to = this.accountRepository.findById(input.to())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (from.getBalance().compareTo(input.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        from.setBalance(from.getBalance().subtract(input.amount()));
        to.setBalance(to.getBalance().add(input.amount()));
        this.accountRepository.save(from);
        this.accountRepository.save(to);
    }

    @Transactional
    @Override
    public void withdrawal(WithdrawalDTO.Input input) {
        if (input == null || input.amount() == null || input.accountId() == null) {
            throw new RuntimeException("Withdrawal input, amount, and accountId cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be greater than zero");
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getBalance().compareTo(input.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        account.setBalance(account.getBalance().subtract(input.amount()));
        this.accountRepository.save(account);
    }

}
