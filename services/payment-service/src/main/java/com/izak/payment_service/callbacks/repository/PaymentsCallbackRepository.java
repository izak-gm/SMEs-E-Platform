package com.izak.payment_service.callbacks.repository;

import com.izak.payment_service.callbacks.entity.PaymentsCallbacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentsCallbackRepository extends JpaRepository<PaymentsCallbacks, UUID> {

  Optional<PaymentsCallbacks> findByTransactionReference(String transactionReference);
}
