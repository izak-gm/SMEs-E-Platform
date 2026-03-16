package com.izak.auth_service.auth.dto;

import java.time.LocalDate;

public record UpdateUser(
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      LocalDate dob,
      String gender
) {
}
