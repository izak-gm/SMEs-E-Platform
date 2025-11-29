package com.izak.payment_service.payment.dto;

import com.izak.payment_service.payment.enums.Method;
import com.izak.payment_service.payment.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentResponse(
      String phoneNumber,
      String transactionReference,
      BigDecimal amount,
      Status status,
      Method method,
      UUID order_id,
      UUID buyer_id
) {
}
