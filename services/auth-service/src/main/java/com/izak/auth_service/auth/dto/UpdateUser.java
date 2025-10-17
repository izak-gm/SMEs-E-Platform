package com.izak.auth_service.auth.dto;

import java.util.Date;

public record UpdateUser(
      String firstName,
      String lastName,
//      String email,
      String phoneNumber,
      Date dob,
      String gender
) {
}
