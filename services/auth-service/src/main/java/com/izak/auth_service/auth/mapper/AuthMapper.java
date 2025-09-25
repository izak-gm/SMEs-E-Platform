package com.izak.auth_service.auth.mapper;

import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.enums.Auth;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMapper {
  private final PasswordEncoder passwordEncoder;

  public User register(RegisterRequest registerRequest) {
    return User.builder()
          .id(registerRequest.id())
          .firstName(registerRequest.firstName())
          .lastName(registerRequest.lastName())
          .email(registerRequest.email())
          .phoneNumber(registerRequest.phoneNumber())
          .dob(registerRequest.dob())
          .gender(registerRequest.gender())
          .auth(Auth.USER)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }
}
