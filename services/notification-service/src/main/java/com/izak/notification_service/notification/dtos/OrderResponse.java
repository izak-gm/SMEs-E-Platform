package com.izak.notification_service.notification.dtos;

import com.izak.notification_service.notification.enums.StatusChoice;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderResponse(
      UUID id,
      UUID buyer_id,
      StatusChoice status,
      BigDecimal total_amount
) {
}
