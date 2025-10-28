package com.izak.auth_service.auth.dto;

import lombok.Builder;

@Builder
public record AuthenticateRequest(
      String email,
      String password) {
}
