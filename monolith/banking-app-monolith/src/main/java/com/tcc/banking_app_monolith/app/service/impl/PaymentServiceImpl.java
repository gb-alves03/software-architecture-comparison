package com.tcc.banking_app_monolith.app.service.impl;

import com.tcc.banking_app_monolith.app.dto.request.PaymentRequestDto;
import com.tcc.banking_app_monolith.app.repository.PaymentRepository;
import com.tcc.banking_app_monolith.app.service.PaymentService;
import com.tcc.banking_app_monolith.domain.entity.Payment;
import com.tcc.banking_app_monolith.domain.enums.PaymentStatus;
import com.tcc.banking_app_monolith.infra.api.antifraud.client.PaymentAntifraudClient;
import com.tcc.banking_app_monolith.infra.api.notification.client.PaymentNotificationClient;
import com.tcc.banking_app_monolith.infra.queue.Queue;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Queue queue;
    private final PaymentAntifraudClient antifraudClient;
    private final PaymentNotificationClient notificationClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              Queue queue,
                              PaymentAntifraudClient antifraudClient,
                              PaymentNotificationClient notificationClient) {
        this.paymentRepository = paymentRepository;
        this.queue = queue;
        this.antifraudClient = antifraudClient;
        this.notificationClient = notificationClient;
    }

    @Override
    public void processPayment(PaymentRequestDto dto) {
        Payment payment = new Payment();
        payment.setAccountId(dto.accountId());
        payment.setAmount(dto.amount());
        payment.setPaymentType(dto.type());
        payment.setStatus(PaymentStatus.PENDING);


        try {
            switch (dto.type()) {
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
            this.queue.publish("payment.exchange", "payment.processed", payment);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            this.paymentRepository.save(payment);
            this.queue.publish("payment.exchange", "payment.failed", payment);
            throw e;
        } finally {
            this.notificationClient.sendNotification(payment);
        }
    }


    private void credit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException();
        }

        if (this.antifraudClient.isFraudulent(payment)) {
            throw new RuntimeException("Payment failed");
        }
    }

    private void debit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException();
        }
    }
}
