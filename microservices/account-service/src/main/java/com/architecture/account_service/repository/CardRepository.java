package com.architecture.account_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.architecture.account_service.model.Card;
import com.architecture.account_service.model.Transaction;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsCardNumber(String number);
    Optional<Card> findByNumber(String number);
}
