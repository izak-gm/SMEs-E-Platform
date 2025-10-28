package com.izak.auth_service.address.dto;

import com.izak.auth_service.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddressRequest(
      Integer id,
      User user,
      @NotNull(message = "Town is required")
      String town,
      @NotNull(message = "City is required")
      String city,
      @NotNull(message = "County is required")
      String county,
      String postalCode
) {
}
