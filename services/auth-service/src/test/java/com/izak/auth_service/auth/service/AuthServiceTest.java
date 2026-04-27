package com.izak.auth_service.auth.service;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.mapper.AuthMapper;
import com.izak.auth_service.configuration.JwtService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.enums.Auth;
import com.izak.auth_service.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
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

  private UUID userId;
  private RegisterRequest registerRequest;
  private RegisterRequest nonExistingUser;
  private User user;
  private String jwtToken;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    registerRequest = new RegisterRequest(
          "test@gmail.com",
          "password"
    );

    nonExistingUser = new RegisterRequest(
          "nonexstinguser@gmail.com",
          "EncodedPassword"
    );

    user = User.builder()
          .id(userId)
          .email("test@gmail.com")
          .auth(Auth.USER)
          .password("encodedPassword")
          .build();
    jwtToken = "test.jwt.token";
  }

  //Test
  @Test
  void shouldRegisterUserAndReturnToken() {
    // mocking the dependencies
    when(authMapper.register(registerRequest)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn(jwtToken);

    // When
    AuthResponse response = authService.register(registerRequest);

    // Then
    assertNotNull(response);
    assertEquals(jwtToken, response.getToken());
    verify(authMapper).register(registerRequest);
    verify(userRepository).save(user);
    verify(jwtService).generateToken(user);

    log.info("Token being passed:{}", jwtToken);
  }

  @Test
  void loginSuccessAndReturnToken() {
    // mocking dependencies
    when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
    when(jwtService.generateToken(user)).thenReturn(jwtToken);

    // When
    AuthResponse response = authService.authenticate(registerRequest);
    // Then
    assertThat(response).isNotNull();
    assertEquals(jwtToken, response.getToken());
    verify(authenticationManager).authenticate(any());
    verify(userRepository).findByEmail("test@gmail.com");
    verify(jwtService).generateToken(user);
  }

  @Test
  void authenticateUserNotFoundTrowException() {
    // Given
    when(userRepository.findByEmail("nonexstinguser@gmail.com")).thenReturn(Optional.empty());

    //When
    assertThatThrownBy(() -> authService.authenticate(nonExistingUser))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessage("User not found");

    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByEmail("nonexstinguser@gmail.com");
    verify(jwtService, never()).generateToken(any());
  }
}