package com.izak.auth_service.auth.dto;

import java.time.LocalDate;
import java.util.UUID;


public record UserResponse(
      UUID id,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      LocalDate dob,
      String gender
) {
}
