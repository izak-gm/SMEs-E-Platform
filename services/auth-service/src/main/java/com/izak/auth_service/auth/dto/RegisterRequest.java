package com.izak.auth_service.auth.dto;

import com.izak.auth_service.user.enums.Auth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record RegisterRequest(
      Long id,
//      @Email
      @NotNull(message = " Email is required")
      String email,
      Auth auth,
      @NotNull(message = "Password is Required")
      String password
) {
}
