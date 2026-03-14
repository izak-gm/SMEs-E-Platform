package com.izak.notification_service.kafka_events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Payment(
      UUID orderId,
      BigDecimal amount,
      String status,
      String paymentMethod,
      Instant createdAt
) {
}