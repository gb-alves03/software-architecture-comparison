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
import com.architecture.account_service.events.PaymentDone;
import com.architecture.account_service.http.AntiFraudService;
import com.architecture.account_service.http.NotificationService;
import com.architecture.account_service.model.Account;
import com.architecture.account_service.model.Owner;
import com.architecture.account_service.model.Transaction;
import com.architecture.account_service.queue.RabbitMQ;
import com.architecture.account_service.repository.AccountRepository;
import com.architecture.account_service.repository.OwnerRepository;
import com.architecture.account_service.repository.TransactionRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final TransactionRepository transactionRepository;
    private final AntiFraudService antiFraudService;
    private final NotificationService notificationService;
    private final RabbitMQ queue;

    public AccountServiceImpl(AccountRepository accountRepository, OwnerRepository ownerRepository, TransactionRepository transactionRepository, AntiFraudService antiFraudService, NotificationService notificationService, RabbitMQ queue) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.transactionRepository = transactionRepository;
        this.antiFraudService = antiFraudService;
        this.notificationService = notificationService;
        this.queue = queue;
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

            this.notificationService.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    @Override
    public void payment(PaymentDTO.Input input) {
        if (input == null || input.accountId() == null || input.amount() == null || input.transactionType() == null) {
            throw new RuntimeException("Payment input, accountId, amount and transactionType cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setType(input.transactionType());
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(input.amount());

        transaction = this.transactionRepository.save(transaction);

        try {
            processPayment(account, input);
            this.accountRepository.save(account);

            PaymentDone event = new PaymentDone(account.getAccountId(), input.amount(), input.transactionType());
            this.queue.send(null, null, event);

            transaction.setStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

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

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(input.amount());

        transaction = this.transactionRepository.save(transaction);

        boolean fraudulent = antiFraudService.isFraudulent(transaction);

        if (fraudulent) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Transaction failed");
        }

        try {
            from.setBalance(from.getBalance().subtract(input.amount()));
            to.setBalance(to.getBalance().add(input.amount()));
            this.accountRepository.save(from);
            this.accountRepository.save(to);

            transaction.setStatus(TransactionStatus.SUCCESS);

            this.notificationService.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
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

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(input.amount());

        this.transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().subtract(input.amount()));
            this.accountRepository.save(account);

            transaction.setStatus(TransactionStatus.SUCCESS);

            this.notificationService.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    private void processPayment(Account account, PaymentDTO.Input input) {
        switch (input.transactionType()) {
        case DEBIT:
            if (account.getBalance().compareTo(input.amount()) < 0) {
                throw new RuntimeException("Insufficient balance for debit transaction");
            }
            account.setBalance(account.getBalance().subtract(input.amount()));
            break;
        case CREDIT:
            break;
        case BANK_SLIP:
            if (account.getBalance().compareTo(input.amount()) < 0) {
                throw new RuntimeException("Insufficient balance for bank slip transaction");
            }
            account.setBalance(account.getBalance().subtract(input.amount()));
            break;
        default:
            throw new RuntimeException("Transaction type not supported");
        }
    }
}
