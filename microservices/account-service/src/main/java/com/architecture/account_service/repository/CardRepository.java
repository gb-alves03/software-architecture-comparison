package com.architecture.account_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.architecture.account_service.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumber(String number);
    Optional<Card> findByNumber(String number);
}
