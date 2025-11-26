package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.*;
import com.tcc.banking_app_monolith.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.app.event.AccountPaymentProcessed;
import com.tcc.banking_app_monolith.app.repository.AccountRepository;
import com.tcc.banking_app_monolith.app.repository.CardRepository;
import com.tcc.banking_app_monolith.app.repository.OwnerRepository;
import com.tcc.banking_app_monolith.app.repository.TransactionRepository;
import com.tcc.banking_app_monolith.app.service.AccountService;
import com.tcc.banking_app_monolith.app.service.PaymentProcessor;
import com.tcc.banking_app_monolith.domain.entity.Account;
import com.tcc.banking_app_monolith.domain.entity.Card;
import com.tcc.banking_app_monolith.domain.entity.Owner;
import com.tcc.banking_app_monolith.domain.entity.Transaction;
import com.tcc.banking_app_monolith.domain.enums.CardType;
import com.tcc.banking_app_monolith.domain.enums.TransactionStatus;
import com.tcc.banking_app_monolith.domain.enums.TransactionType;
import com.tcc.banking_app_monolith.infra.api.antifraud.client.AccountAntifraudClient;
import com.tcc.banking_app_monolith.infra.api.notification.client.AccountNotificationClient;
import com.tcc.banking_app_monolith.infra.queue.AccountQueue;
import com.tcc.banking_app_monolith.utils.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final AccountNotificationClient accountNotificationClient;
    private final AccountAntifraudClient accountAntifraudClient;
    private final AccountQueue accountQueue;

    private static final SecureRandom random = new SecureRandom();

    private final Map<TransactionType, PaymentProcessor> paymentProcessors;

    public AccountServiceImpl(AccountRepository accountRepository,
                              OwnerRepository ownerRepository,
                              CardRepository cardRepository,
                              TransactionRepository transactionRepository,
                              AccountNotificationClient accountNotificationClient,
                              AccountAntifraudClient accountAntifraudClient, List<PaymentProcessor> processors,
                              AccountQueue accountQueue) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.accountNotificationClient = accountNotificationClient;
        this.accountAntifraudClient = accountAntifraudClient;
        this.paymentProcessors = processors.stream()
                .collect(Collectors.toMap(PaymentProcessor::getType, paymentProcessor -> paymentProcessor));
        this.accountQueue = accountQueue;
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

        Card card = generateNewCard(account, CardType.CREDIT, new BigDecimal(70000));
        account.setCard(card);

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
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getValue());
        }

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));

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

            this.accountNotificationClient.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    @Override
    public void withdrawal(WithdrawalRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getValue());

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));

        if (account.getBalance().compareTo(dto.amount()) < 0) throw new RuntimeException(Constants.INSUFFICIENT_BALANCE.getValue());

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

            this.accountNotificationClient.sendNotification(transaction);
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
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getValue());
        }

        Account from = this.accountRepository.findById(dto.from())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));
        Account to = this.accountRepository.findById(dto.to())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));

        if (from.getBalance().compareTo(dto.amount()) < 0) {
            throw new RuntimeException(Constants.INSUFFICIENT_BALANCE.getValue());
        }

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setFrom(from);
        transaction.setTo(to);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        transaction = this.transactionRepository.save(transaction);

        throwFailedTransactionByAntifraud(transaction);
        processTransferring(dto, from, to, transaction);
    }

    @Override
    public void payment(AccountPaymentRequestDto dto) {
        if (dto.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATER_THAN_ZERO.getValue());
        }

        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));

        Transaction transaction = new Transaction();
        transaction.setType(dto.transactionType());
        transaction.setFrom(account);
        transaction.setTo(account);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAmount(dto.amount());

        transaction = this.transactionRepository.save(transaction);

        try {
            throwFailedTransactionByAntifraud(transaction);
            processPayment(account, dto);
            this.accountRepository.save(account);

            AccountPaymentProcessed event = new AccountPaymentProcessed(transaction.getId(), account.getId(), dto.amount(), dto.transactionType());
            this.accountQueue.publish(Constants.PAYMENT_EXCHANGE.getValue(), Constants.PAYMENT_PROCESSED_ROUTING_KEY.getValue(), event);

            transaction.setStatus(TransactionStatus.SUCCESS);
            this.transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            this.transactionRepository.save(transaction);
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    @Override
    public void paymentSuccess(PaymentProcessedRequestDto dto) {
        Transaction transaction = this.transactionRepository.findById(dto.transactionId())
                .orElseThrow(() -> new RuntimeException(Constants.TRANSACTION_NOT_FOUND.getValue()));
        transaction.setStatus(TransactionStatus.SUCCESS);
        this.transactionRepository.save(transaction);
    }

    @Override
    public void paymentFailed(PaymentProcessedRequestDto dto) {
        Account account = this.accountRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND.getValue()));

        if (TransactionType.DEBIT.equals(dto.type())) {
            account.setBalance(account.getBalance().add(dto.amount()));
        }

        if (TransactionType.CREDIT.equals(dto.type())) {
            account.getCard().setCreditLimit(account.getCard().getCreditLimit().add(dto.amount()));
            this.cardRepository.save(account.getCard());
        }

        Transaction transaction = this.transactionRepository.findById(dto.transactionId())
                .orElseThrow(() -> new RuntimeException(Constants.TRANSACTION_NOT_FOUND.getValue()));
        transaction.setStatus(TransactionStatus.FAILED);
        this.transactionRepository.save(transaction);
    }

    private Card generateNewCard(Account account, CardType type, BigDecimal limit) {
        while (true) {
            String number = generateCardNumber();
            String cvv = generateCvv();

            if (!this.cardRepository.existsByNumber(number)) {
                Card card = new Card();
                card.setNumber(number);
                card.setCvv(cvv);
                card.setType(type);
                card.setAccount(account);
                card.setCreditLimit(limit);
                return card;
            }
        }
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

    private void throwFailedTransactionByAntifraud(Transaction transaction) {
        if (accountAntifraudClient.isFraudulent(transaction)) {
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

            this.accountNotificationClient.sendNotification(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException(e.getMessage());
        } finally {
            this.transactionRepository.save(transaction);
        }
    }

    private void processPayment(Account account, AccountPaymentRequestDto dto) {
        PaymentProcessor processor = paymentProcessors.get(dto.transactionType());

        if (processor == null) throw new RuntimeException(Constants.TRANSACTION_TYPE_NOT_SUPPORTED.getValue());

        processor.process(account, dto);
    }
}
