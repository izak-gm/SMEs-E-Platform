package com.izak.auth_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
      @Email
      @NotNull(message = " Email is required")
      String email,
      @NotNull(message = "Password is Required")
      String password
) {
}
