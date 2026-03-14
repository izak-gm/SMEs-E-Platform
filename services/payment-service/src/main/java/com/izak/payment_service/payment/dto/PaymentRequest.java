package com.izak.payment_service.payment.dto;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
      UUID id,
      @Positive
      BigDecimal amount,
      @NotNull(message = "Phone Number cannot be empty")
      String phoneNumber,
      UUID orderId,
      UUID buyerId,
      PaymentMethod paymentMethod,
      PaymentStatus paymentStatus
) {
}
