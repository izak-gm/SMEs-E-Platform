package com.izak.auth_service.auth.service;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.AuthenticateRequest;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.mapper.AuthMapper;
import com.izak.auth_service.configuration.JwtService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.enums.Auth;
import com.izak.auth_service.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  // mock the dependencies
  @Mock
  private UserRepository userRepository;
  @Mock
  private JwtService jwtService;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private AuthMapper authMapper;

  //inject Mocks in the class
  @InjectMocks
  private AuthService authService;

  //Test
  @Test
  void shouldRegisterUserAndReturnToken() {
    // Given
    RegisterRequest registerRequest = new RegisterRequest(
          UUID.randomUUID(),
          "test@gmail.com",
          Auth.USER,
          "password"
    );

    User user = User.builder().email("test@gmail.com").build();

    // mocking the dependencies
    when(authMapper.register(registerRequest)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn("mocked-jwt");

    AuthResponse response = authService.register(registerRequest);

    // When
    assertNotNull(response);
    assertEquals("mocked-jwt", response.getToken());

    verify(userRepository).save(user);
    verify(jwtService).generateToken(user);

  }

  @Test
  void authenticate() {
    // Given
    AuthenticateRequest request = new AuthenticateRequest(
          "test@gmail.com",
          "password"
    );

    // Build the user
    User user = User.builder().email("test@gmail.com").build();

    // mocking dependencies
    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    AuthResponse response = authService.authenticate(request);
    // When
    assertEquals("jwt-token", response.getToken());

    // Then
    verify(authenticationManager).authenticate(any());
  }
}