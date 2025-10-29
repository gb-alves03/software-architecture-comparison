package com.tcc.banking_app_monolith.account_management.app.controller;

import com.tcc.banking_app_monolith.account_management.app.dto.request.*;
import com.tcc.banking_app_monolith.account_management.app.dto.request.*;
import com.tcc.banking_app_monolith.account_management.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.account_management.app.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterRequestDto dto) {
        try {
            RegisterResponseDto response = this.accountService.register(dto);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getLocalizedMessage());
        }
    }

    @PostMapping("/transactions/deposit")
    public ResponseEntity<String> deposit(@RequestBody @Valid DepositRequestDto dto) {
        try {
            this.accountService.deposit(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/payments")
    public ResponseEntity<String> payment(@RequestBody @Valid PaymentRequestDto dto) {
        try {
            this.accountService.payment(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<String> transfer(@RequestBody @Valid TransferRequestDto dto) {
        try {
            this.accountService.transfer(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/transactions/withdrawal")
    public ResponseEntity<String> withdrawal(@RequestBody @Valid WithdrawalRequestDto dto) {
        try {
            this.accountService.withdrawal(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
