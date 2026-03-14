package com.izak.auth_service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public record UserRespose(
      UUID id,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      Date dob,
      String gender
) {
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
  private String filter;
  private Integer page;
  private Integer size;
}
