package com.architecture.account_service.service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.PaymentProcessedDTO;
import com.architecture.account_service.dto.PaymentProcessedDTO.Input;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;
import com.architecture.account_service.enumeration.CardType;
import com.architecture.account_service.enumeration.TransactionStatus;
import com.architecture.account_service.enumeration.TransactionType;
import com.architecture.account_service.event.PaymentProcessed;
import com.architecture.account_service.http.AntiFraudService;
import com.architecture.account_service.http.NotificationService;
import com.architecture.account_service.model.Account;
import com.architecture.account_service.model.Card;
import com.architecture.account_service.model.Owner;
import com.architecture.account_service.model.Transaction;
import com.architecture.account_service.queue.Queue;
import com.architecture.account_service.repository.AccountRepository;
import com.architecture.account_service.repository.CardRepository;
import com.architecture.account_service.repository.OwnerRepository;
import com.architecture.account_service.repository.TransactionRepository;
import com.architecture.account_service.utils.Constants;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final AntiFraudService antiFraudService;
    private final NotificationService notificationService;
    private final Queue queue;

    private static final SecureRandom random = new SecureRandom();

    public AccountServiceImpl(AccountRepository accountRepository, OwnerRepository ownerRepository, TransactionRepository transactionRepository, CardRepository cardRepository, AntiFraudService antiFraudService, NotificationService notificationService, Queue queue) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
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
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND));

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

    @Transactional
    @Override
    public void payment(PaymentDTO.Input input) {
        if (input == null || input.accountId() == null || input.amount() == null || input.transactionType() == null) {
            throw new RuntimeException("Payment input, accountId, amount and transactionType cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND));

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

            PaymentProcessed event = new PaymentProcessed(transaction.getTransactionId(), account.getAccountId(),
                    input.amount(), input.transactionType());
            this.queue.publish(Constants.PAYMENT_EXCHANGE, Constants.PAYMENT_PROCESSED_ROUTING_KEY, event);

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
        account.setCards(new ArrayList<Card>());

        Card card = generateNewCard(account, CardType.CREDIT, new BigDecimal(1000));
        account.getCards().add(card);

        this.ownerRepository.save(owner);
        this.accountRepository.save(account);
        this.cardRepository.save(card);
    }

    @Transactional
    @Override
    public void transfer(TransferDTO.Input input) {
        if (input == null || input.from() == null || input.to() == null || input.amount() == null) {
            throw new RuntimeException("Transper input, from, to and amount cannot be null");
        }
        if (input.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }
        Account from = this.accountRepository.findById(input.from())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND));
        Account to = this.accountRepository.findById(input.to())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND));

        if (from.getBalance().compareTo(input.amount()) < 0) {
            throw new RuntimeException(Constants.INSUFFICIENT_BALANCE);
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
            throw new RuntimeException(Constants.TRANSACTION_FAILED);
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
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }
        Account account = this.accountRepository.findById(input.accountId())
                .orElseThrow(() -> new RuntimeException(Constants.ACCOUNT_NOT_FOUND));

        if (account.getBalance().compareTo(input.amount()) < 0) {
            throw new RuntimeException(Constants.INSUFFICIENT_BALANCE);
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

    @Transactional
    @Override
    public void paymentSuccess(PaymentProcessedDTO.Input input) {
        Transaction transaction = this.transactionRepository.findById(input.transactionId()).orElseThrow(() -> new RuntimeException(Constants.TRANSACTION_NOT_FOUND));
        transaction.setStatus(TransactionStatus.SUCCESS);
        this.transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void paymentFailed(PaymentProcessedDTO.Input input) {
        // Ações compensatórias para débito e crédito
        Transaction transaction = this.transactionRepository.findById(input.transactionId()).orElseThrow(() -> new RuntimeException(Constants.TRANSACTION_NOT_FOUND));
        transaction.setStatus(TransactionStatus.FAILED);
        this.transactionRepository.save(transaction);
    }

    private void processPayment(Account account, PaymentDTO.Input input) {
        switch (input.transactionType()) {
        case DEBIT:
            if (account.getBalance().compareTo(input.amount()) < 0) {
                throw new RuntimeException(Constants.INSUFFICIENT_BALANCE);
            }
            account.setBalance(account.getBalance().subtract(input.amount()));
            break;
        case CREDIT:
            List<Card> cards = account.getCards();

            for (Card card : cards) {
                if (card.getCreditLimit().compareTo(input.amount()) < 0) {
                    throw new RuntimeException(Constants.INSUFFICIENT_BALANCE);
                }
                card.setCreditLimit(card.getCreditLimit().subtract(input.amount()));
                break;
            }
            break;
        default:
            throw new RuntimeException(Constants.TRANSACTION_TYPE_NOT_SUPPORTED);
        }
    }

    private Card generateNewCard(Account account, CardType type, BigDecimal limit) {
        while (true) {
            String number = generateCardNumber();
            String cvv = generateCvv();

            if (!this.cardRepository.existsCardNumber(number)) {
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

}
