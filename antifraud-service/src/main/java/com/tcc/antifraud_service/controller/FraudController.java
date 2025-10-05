package com.tcc.antifraud_service.controller;

import com.tcc.antifraud_service.dto.FraudRequest;
import com.tcc.antifraud_service.dto.FraudResponse;
import com.tcc.antifraud_service.service.FraudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/fraud")
public class FraudController {

    private final FraudService service;

    public FraudController(FraudService service) {
        this.service = service;
    }

    @PostMapping("/validate")
    public ResponseEntity<FraudResponse> validate(@RequestBody FraudRequest request) {
        FraudResponse response = service.validate(request);
        return ResponseEntity.ok(response);
    }
}
