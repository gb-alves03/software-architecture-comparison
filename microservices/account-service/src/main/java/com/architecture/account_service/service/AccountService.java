package com.architecture.account_service.service;

import com.architecture.account_service.dto.DepositDTO;
import com.architecture.account_service.dto.PaymentDTO;
import com.architecture.account_service.dto.PaymentProcessedDTO;
import com.architecture.account_service.dto.RegisterDTO;
import com.architecture.account_service.dto.TransferDTO;
import com.architecture.account_service.dto.WithdrawalDTO;

public interface AccountService {
    public RegisterDTO.Output register(RegisterDTO.Input input);

    public void transfer(TransferDTO.Input input);

    public void deposit(DepositDTO.Input input);

    public void withdrawal(WithdrawalDTO.Input input);

    public void payment(PaymentDTO.Input input);
    
    public void paymentSuccess(PaymentProcessedDTO.Input input);
    
    public void paymentFailed(PaymentProcessedDTO.Input input);
}
