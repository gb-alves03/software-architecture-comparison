package com.tcc.banking_app_monolith.account_management.app.service;

import com.tcc.banking_app_monolith.account_management.app.dto.request.*;
import com.tcc.banking_app_monolith.app.dto.request.*;
import com.tcc.banking_app_monolith.account_management.app.dto.response.RegisterResponseDto;

public interface AccountService {

    RegisterResponseDto register(RegisterRequestDto dto);
    void deposit(DepositRequestDto dto);
    void withdrawal(WithdrawalRequestDto dto);
    void transfer(TransferRequestDto dto);
    void payment(PaymentRequestDto dto);
}
