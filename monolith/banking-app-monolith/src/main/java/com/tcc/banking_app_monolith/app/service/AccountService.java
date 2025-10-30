package com.tcc.banking_app_monolith.app.service;

import com.tcc.banking_app_monolith.account_management.app.dto.account.request.*;
import com.tcc.banking_app_monolith.app.dto.response.RegisterResponseDto;
import com.tcc.banking_app_monolith.app.dto.request.*;

public interface AccountService {

    RegisterResponseDto register(RegisterRequestDto dto);
    void deposit(DepositRequestDto dto);
    void withdrawal(WithdrawalRequestDto dto);
    void transfer(TransferRequestDto dto);
    void payment(AccountPaymentRequestDto dto);
}
