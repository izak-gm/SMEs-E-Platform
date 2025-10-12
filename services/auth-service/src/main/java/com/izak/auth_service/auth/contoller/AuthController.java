package com.izak.auth_service.auth.contoller;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.AuthenticateRequest;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("register")
  public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
    return ResponseEntity.ok(authService.register(registerRequest));
  };

  @PostMapping("login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest){
    return ResponseEntity.ok(authService.authenticate(authenticateRequest));
  };
}
