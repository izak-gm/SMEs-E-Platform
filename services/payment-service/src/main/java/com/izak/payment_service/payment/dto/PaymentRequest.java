package com.izak.payment_service.payment.dto;

import com.izak.payment_service.payment.enums.Method;
import com.izak.payment_service.payment.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
      UUID id,
      @NotNull(message ="Amount cannot be empty")
      @Positive
      BigDecimal amount,
      @NotNull(message ="Transaction Reference cannot be empty")
      String transactionReference,
      @NotNull(message ="Phone Number cannot be empty")
      String phoneNumber,
      @NotNull(message="Order id should not be null")
      UUID orderId,
      @NotNull(message="Buyer id should not be null")
      UUID buyerId,
      Method method,
      Status status
      ) {
}
