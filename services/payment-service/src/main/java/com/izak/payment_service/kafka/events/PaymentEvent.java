package com.izak.payment_service.kafka.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentEvent(
      UUID orderId,
      BigDecimal amount,
      String status,
      String paymentMethod,
      Instant createdAt
) {
}
