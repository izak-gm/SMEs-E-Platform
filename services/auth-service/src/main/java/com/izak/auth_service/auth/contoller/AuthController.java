package com.izak.auth_service.auth.contoller;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.AuthenticateRequest;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.dto.UpdateUser;
import com.izak.auth_service.auth.service.AuthService;
import com.izak.auth_service.user.entity.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("register")
  public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
    return ResponseEntity.ok(authService.register(registerRequest));
  }
  @PostMapping("admin/register")
  public ResponseEntity<AuthResponse> registerSeller(@RequestBody @Valid RegisterRequest registerRequest){
    return ResponseEntity.ok(authService.registerSeller(registerRequest));
  }
  @PostMapping("seller/register")
  public ResponseEntity<AuthResponse> registerAdmin(@RequestBody @Valid RegisterRequest registerRequest){
    return ResponseEntity.ok(authService.registerAdmin(registerRequest));
  }

  @PostMapping("login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest){
    return ResponseEntity.ok(authService.authenticate(authenticateRequest));
  }

  @PutMapping("user/update-profile/{userId}")
  public ResponseEntity<User> updateProfile(
        @RequestBody UpdateUser updateRequest,
        @PathVariable Long id
  ){
    User updateUser=authService.updateProfile(id,updateRequest);
    return ResponseEntity.ok(updateUser);
  }
}
