package com.architecture.account_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.architecture.account_service.dto.CreditDTO;
import com.architecture.account_service.dto.DebitDTO;
import com.architecture.account_service.dto.ProfileDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.service.AccountService;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegisterDTO.Input input) {
        try {
            this.accountService.register(input);
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/debit")
    public ResponseEntity<String> debit(@RequestBody DebitDTO.Input input) {
        try {
            this.accountService.debit(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/credit")
    public ResponseEntity<String> credit(@RequestBody CreditDTO.Input input) {
        try {
            this.accountService.credit(input);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> findAccountById(@PathVariable Long accountId) {
        try {
            ProfileDTO.Output output = this.accountService.findAccountById(new ProfileDTO.Input(accountId));
            return ResponseEntity.status(HttpStatus.OK).body(output);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }
}
