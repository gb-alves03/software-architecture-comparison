package com.tcc.payment_service.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.payment_service.dto.PaymentDTO;
import com.tcc.payment_service.enumeration.PaymentStatus;
import com.tcc.payment_service.model.Payment;
import com.tcc.payment_service.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
            this.paymentRepository.save(payment);
            // Realiza a notificação
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            this.paymentRepository.save(payment);
            throw e;
        }

    }

    private void credit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException();
        }

        // validação do limite do cartão ?

        // comunicação com o antifraud service

        payment.setStatus(PaymentStatus.SUCCESS);
        // Dispara evento para atualizar a transação do account service
    }

    private void debit(Payment payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException();
        }

        // validação do saldo da conta ?

        payment.setStatus(PaymentStatus.SUCCESS);
        // Dispara evento para atualiza a transação do account service
    }

    private void bankSlip(Payment payment) {
        // Que tipo de regras teriamos aqui ?
    }

}
