package com.tcc.banking_app_monolith.account_management.app.repository;

import com.tcc.banking_app_monolith.account_management.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
