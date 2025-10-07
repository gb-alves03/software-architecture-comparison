package com.architecture.account_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;
import com.architecture.account_service.service.AccountServiceImpl;

@RestController
@RequestMapping("/v1/accounts")
public class AccountControllerImpl implements AccountController {
    private final AccountServiceImpl accountService;

    public AccountControllerImpl(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Override
    public ResponseEntity<String> register(RegisterDTO.Input input) {
        try {
            this.accountService.register(input);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/transactions/deposit")
    @Override
    public ResponseEntity<String> deposit(DepositDTO.Input input) {
        try {
            this.accountService.deposit(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/payments")
    @Override
    public ResponseEntity<String> payment(PaymentDTO.Input input) {
        // TODO Auto-generated method stub
        return null;
    }

    @PostMapping("/transactions/transfer")
    @Override
    public ResponseEntity<String> transfer(TransferDTO.Input input) {
        // TODO Auto-generated method stub
        return null;
    }

    @PostMapping("/transactions/withdrawal")
    @Override
    public ResponseEntity<String> withdrawal(WithdrawalDTO.Input input) {
        // TODO Auto-generated method stub
        return null;
    }

}
