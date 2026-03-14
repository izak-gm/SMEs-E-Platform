package com.izak.notification_service.notification.dtos;

import java.util.UUID;

public record BuyerResponse(
      UUID id,
      String firstName,
      String lastName,
      String gender,
      String email,
      String phoneNumber
) {
}
