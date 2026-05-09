package com.izak.payment_service.payment.dto;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentResponse(
      BigDecimal amount,
      String phoneNumber,
      String transactionReference,
      UUID order_id,
      UUID buyer_id,
      PaymentStatus paymentStatus,
      PaymentMethod paymentMethod
) {
}
