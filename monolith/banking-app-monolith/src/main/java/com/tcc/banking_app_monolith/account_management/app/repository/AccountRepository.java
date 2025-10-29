package com.tcc.banking_app_monolith.account_management.app.repository;

import com.tcc.banking_app_monolith.account_management.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
