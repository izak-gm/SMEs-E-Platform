package com.izak.auth_service.auth.dto;

import java.util.Date;
import java.util.UUID;


public record UserResponse(
      UUID id,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      Date dob,
      String gender
) {
}
