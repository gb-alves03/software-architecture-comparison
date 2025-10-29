package com.tcc.banking_app_monolith.account_management.app.service.impl;

import com.tcc.banking_app_monolith.account_management.app.dto.request.*;
import com.tcc.banking_app_monolith.app.dto.request.*;
import com.tcc.banking_app_monolith.account_management.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.account_management.app.events.PaymentDone;
import com.tcc.banking_app_monolith.account_management.app.repository.AccountRepository;
import com.tcc.banking_app_monolith.account_management.app.repository.CardRepository;
import com.tcc.banking_app_monolith.account_management.app.repository.OwnerRepository;
import com.tcc.banking_app_monolith.account_management.app.repository.TransactionRepository;
import com.tcc.banking_app_monolith.account_management.app.service.AccountService;
import com.tcc.banking_app_monolith.account_management.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.account_management.domain.entity.Account;
import com.tcc.banking_app_monolith.account_management.domain.entity.Card;
import com.tcc.banking_app_monolith.account_management.domain.entity.Owner;
import com.tcc.banking_app_monolith.account_management.domain.entity.Transaction;
import com.tcc.banking_app_monolith.account_management.domain.enums.CardType;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionStatus;
import com.tcc.banking_app_monolith.account_management.domain.enums.TransactionType;
import com.tcc.banking_app_monolith.account_management.infra.api.antifraud.client.AntifraudClient;
import com.tcc.banking_app_monolith.account_management.infra.api.notification.client.NotificationClient;
import com.tcc.banking_app_monolith.account_management.queue.Queue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationClient notificationClient;
    private final AntifraudClient antifraudClient;
    private final Queue queue;

    private static final SecureRandom random = new SecureRandom();

    private final Map<TransactionType, PaymentProcessor> paymentProcessors;

    public AccountServiceImpl(AccountRepository accountRepository,
                              OwnerRepository ownerRepository,
                              CardRepository cardRepository,
                              TransactionRepository transactionRepository,
                              NotificationClient notificationClient,
                              AntifraudClient antifraudClient, List<PaymentProcessor> processors,
                              Queue queue) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.notificationClient = notificationClient;
        this.antifraudClient = antifraudClient;
        this.paymentProcessors = processors.stream()
                .collect(Collectors.toMap(PaymentProcessor::getType, paymentProcessor -> paymentProcessor));
        this.queue = queue;
    }


    @Transactional
    public RegisterResponseDto register(RegisterRequestDto dto) {

        if (ownerRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

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

    @Transactional
    @Override
    public void deposit(DepositRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        this.transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().add(dto.amount()));
            this.accountRepository.save(account);

            transaction.setStatus(TransactionStatus.SUCCESS);

            this.notificationClient.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    @Override
    public void withdrawal(WithdrawalRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("Withdrawal amount must be greater than zero");

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(dto.amount()) < 0) throw new RuntimeException("Insufficient balance");

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        this.transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().subtract(dto.amount()));
            this.accountRepository.save(account);

            transaction.setStatus(TransactionStatus.SUCCESS);

            this.notificationClient.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    @Override
    public void transfer(TransferRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than zero");
        }

        Account from = this.accountRepository.findById(dto.from())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account to = this.accountRepository.findById(dto.to())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (from.getBalance().compareTo(dto.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        transaction = this.transactionRepository.save(transaction);

        throwFailedTransferringByAntifraud(transaction);
        processTransferring(dto, from, to, transaction);
    }

    @Override
    public void payment(PaymentRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setType(dto.transactionType());
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        transaction = this.transactionRepository.save(transaction);

        try {
            processPayment(account, dto);
            this.accountRepository.save(account);

            PaymentDone event = new PaymentDone(account.getId(), dto.amount(), dto.transactionType());
            this.queue.publish(PaymentDone.queue, null, event);

            transaction.setStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    private Card generateNewCard(Account account, CardType type, BigDecimal limit) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            String number = generateCardNumber();
            String cvv = generateCvv();

            if (this.cardRepository.existsByNumber(number)) {
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

    private void throwFailedTransferringByAntifraud(Transaction transaction) {
        if (antifraudClient.isFraudulent(transaction)) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
        }
    }

    private void processTransferring(TransferRequestDto dto, Account from, Account to, Transaction transaction) {
        try {
            from.setBalance(from.getBalance().subtract(dto.amount()));
            to.setBalance(to.getBalance().add(dto.amount()));
            this.accountRepository.save(from);
            this.accountRepository.save(to);

            transaction.setStatus(TransactionStatus.SUCCESS);

            this.notificationClient.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    private void processPayment(Account account, PaymentRequestDto dto) {
        PaymentProcessor processor = paymentProcessors.get(dto.transactionType());

        if (processor == null) throw new RuntimeException("Transaction type not supported");

        processor.process(account, dto);
    }
}
