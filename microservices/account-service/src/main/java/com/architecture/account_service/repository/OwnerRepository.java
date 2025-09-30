package com.architecture.account_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.architecture.account_service.model.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

}
