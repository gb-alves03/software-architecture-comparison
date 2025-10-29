package com.tcc.banking_app_monolith.app.repository;

import com.tcc.banking_app_monolith.domain.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByNumber(String number);
    Optional<Card> findByNumber(String number);
}
