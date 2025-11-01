package com.tcc.payment_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.payment_service.queue.Queue;
import com.tcc.payment_service.dto.PaymentDTO;
import com.tcc.payment_service.enumeration.PaymentStatus;
import com.tcc.payment_service.event.PaymentProcessed;
import com.tcc.payment_service.http.AntiFraudService;
import com.tcc.payment_service.http.NotificationService;
import com.tcc.payment_service.model.Payment;
import com.tcc.payment_service.repository.PaymentRepository;
import com.tcc.payment_service.utils.Constants;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Queue queue;
    private final AntiFraudService antiFraudService;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, Queue queue, AntiFraudService antiFraudService, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.queue = queue;
        this.antiFraudService = antiFraudService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void processPayment(PaymentDTO.Input input) {
        Payment payment = new Payment();
        payment.setAccountId(input.accountId());
        payment.setAmount(input.amount());
        payment.setPaymentType(input.type());
        payment.setStatus(PaymentStatus.PENDING);

        try {
            switch (input.type()) {
            case CREDIT:
                credit(payment);
                break;
            case DEBIT:
                debit(payment);
                break;
            default:
                throw new RuntimeException();
            }
            payment.setStatus(PaymentStatus.SUCCESS);
            this.paymentRepository.save(payment);
            this.queue.publish(Constants.PAYMENT_EXCHANGE, Constants.PAYMENT_SUCCESS_ROUTING_KEY,
                    new PaymentProcessed(input.transactionId(), PaymentStatus.SUCCESS));
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            this.paymentRepository.save(payment);
            this.queue.publish(Constants.PAYMENT_EXCHANGE, Constants.PAYMENT_FAILED_ROUTING_KEY,
                    new PaymentProcessed(input.transactionId(), PaymentStatus.FAILED));
            throw e;
        } finally {
            this.notificationService.sendNotification(payment);
        }

    }

    private void credit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }

        if (this.antiFraudService.isFraudulent(payment)) {
            throw new RuntimeException(Constants.PAYMENT_FAILED);
        }
    }

    private void debit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(Constants.AMOUNT_MUST_BE_GREATHER_THAN_ZERO);
        }
    }
}
