package com.izak.auth_service.auth.mapper;

import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.dto.UpdateUser;
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
          .email(registerRequest.email())
          .auth(Auth.USER)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }  public User register(RegisterRequest registerRequest) {
    return User.builder()
          .id(registerRequest.id())
          .email(registerRequest.email())
          .auth(Auth.SELLER)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }
  public User registerAdmin(RegisterRequest registerRequest) {
    return User.builder()
          .id(registerRequest.id())
          .email(registerRequest.email())
          .auth(Auth.ADMIN)
          .password(passwordEncoder.encode(registerRequest.password()))
          .build();
  }
  public User updateUser(UpdateUser updateUser) {
    return User.builder()
          .firstName(updateUser.firstName())
          .lastName(updateUser.lastName())
          .dob(updateUser.dob())
          .gender(updateUser.gender())
          .phoneNumber(updateUser.phoneNumber())
          .build();
  }
}
