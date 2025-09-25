package com.izak.auth_service.auth.service;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.AuthenticateRequest;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.mapper.AuthMapper;
import com.izak.auth_service.configuration.JwtService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final AuthMapper authMapper;

  public AuthResponse register(RegisterRequest registerRequest) {
    User user=userRepository.save(authMapper.register(registerRequest));
    var jwtToken=jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public AuthResponse authenticate(AuthenticateRequest authenticateRequest) {
return null;
  };
}
