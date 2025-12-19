package com.izak.payment_service.OrderEvent.repository;

import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentIntentRepository
      extends JpaRepository<PaymentIntent, UUID> {

  Optional<PaymentIntent> findByOrderId(UUID orderId);
}
