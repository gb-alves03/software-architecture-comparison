package com.architecture.account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;

public interface AccountController {
    public ResponseEntity<String> register(@RequestBody RegisterDTO.Input input);

    public ResponseEntity<String> transfer(@RequestBody TransferDTO.Input input);

    public ResponseEntity<String> deposit(@RequestBody DepositDTO.Input input);

    public ResponseEntity<String> withdrawal(@RequestBody WithdrawalDTO.Input input);

    public ResponseEntity<String> payment(@RequestBody PaymentDTO.Input input);
}
