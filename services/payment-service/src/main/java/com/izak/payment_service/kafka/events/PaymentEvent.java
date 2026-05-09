package com.izak.payment_service.kafka.events;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentEvent(
      UUID orderId,
      BigDecimal amount,
      PaymentStatus paymentStatus,
      PaymentMethod paymentMethod,
      Instant createdAt
) {
}
