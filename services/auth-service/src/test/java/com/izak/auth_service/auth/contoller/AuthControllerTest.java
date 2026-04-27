package com.izak.auth_service.auth.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.dto.UpdateUser;
import com.izak.auth_service.auth.dto.UserResponse;
import com.izak.auth_service.auth.service.AuthService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.enums.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
  private MockMvc mockMvc;
  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  private ObjectMapper objectMapper;
  private UUID userId;
  private RegisterRequest registerRequest;
  private AuthResponse authResponse;
  private User user;
  private UpdateUser updateUser;
  private UserResponse userResponse;
  private String jwtToken;
  private Auth auth;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders
          .standaloneSetup(authController)
          .build();

    userId = UUID.randomUUID();
    registerRequest = new RegisterRequest(
          "testisaac@gmail.com",
          "encodedPassword"
    );
    jwtToken = "test.jwt.token";

    authResponse = AuthResponse.builder()
          .token(jwtToken)
          .build();
    user = User.builder()
          .id(userId)
          .email("testisaac@gmai.com")
          .auth(Auth.USER)
          .firstName("Isaac")
          .lastName("izak")
          .phoneNumber("1234567890")
          .build();

    updateUser = new UpdateUser(
          "John",
          "Doe",
          "testisaac@gmai.com",
          "1234567890",
          LocalDate.of(1990, 1, 1),
          "MALE"
    );
    userResponse = new UserResponse(
          userId,
          "Isaac",
          "izak",
          "testisaac@gmail.com",
          "1234567890",
          LocalDate.of(1990, 1, 1),
          "MALE",
          Auth.USER
    );
  }

  @Test
  void shouldRegisterUserReturnJwtToken() throws Exception {
    when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

    // When & Then
    mockMvc.perform(post("/v1/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value(jwtToken));
    verify(authService, times(1)).register(any(RegisterRequest.class));
  }


  @Test
  void shouldLoginUser() throws Exception {
    when(authService.authenticate(any())).thenReturn(authResponse);

    assertEquals(jwtToken, authResponse.getToken());

    mockMvc.perform(post("/v1/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").value(jwtToken));
  }


}