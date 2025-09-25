package com.izak.auth_service.auth.dto;

import com.izak.auth_service.user.enums.Auth;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record RegisterRequest(
      Integer id,
      @NotNull(message = "First name is required")
      String firstName,
      @NotNull(message = "Last name is required")
      String lastName,
      @NotNull(message = " Email is required")
      String email,
      @NotNull(message = "Phone Number is required")
      String phoneNumber,
      @NotNull(message = "dob is required")
      Date dob,
      String gender,
      Auth auth,
      String password
) {
}
