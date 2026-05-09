package com.izak.payment_service.payment.dto;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
      @Positive
      BigDecimal amount,

      @NotBlank(message = "Phone Number cannot be empty")
      @Pattern(regexp = "^2547\\d{8}$", message = "Invalid phone number format")
      String phoneNumber,

      @NotNull
      UUID orderId,

      @NotNull
      UUID buyerId,

      @NotNull
      PaymentMethod paymentMethod,

      @NotNull
      PaymentStatus paymentStatus
) {
}
