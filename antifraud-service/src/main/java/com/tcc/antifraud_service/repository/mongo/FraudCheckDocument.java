package com.tcc.antifraud_service.repository.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "fraud_checks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FraudCheckDocument {

    @Id
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String transactionType;
    private boolean fraudulent;
    private String reason;
}
