package com.izak.auth_service.auth.contoller;

import com.izak.auth_service.auth.dto.*;
import com.izak.auth_service.auth.service.AuthService;
import com.izak.auth_service.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("v1/api/auth")
@AllArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("register")
  public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.register(registerRequest));
  }

  @PostMapping("admin/register")
  public ResponseEntity<AuthResponse> registerSeller(@RequestBody @Valid RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.registerSeller(registerRequest));
  }

  @PostMapping("seller/register")
  public ResponseEntity<AuthResponse> registerAdmin(@RequestBody @Valid RegisterRequest registerRequest) {
    return ResponseEntity.ok(authService.registerAdmin(registerRequest));
  }

  @PostMapping("login")
  public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest) {
    return ResponseEntity.ok(authService.
          authenticate(authenticateRequest));
  }

  @PutMapping("user/update-profile/{userId}")
  public ResponseEntity<User> updateProfile(
        @RequestBody UpdateUser updateRequest,
        @PathVariable("userId") UUID id
  ) {
    User updateUser = authService.updateProfile(id, updateRequest);
    return ResponseEntity.ok(updateUser);
  }

  @GetMapping("users")
  public List<UserResponse> getUsersByIds(
        @RequestParam(required = false) List<UUID> ids,
        @ModelAttribute UserRequest userRequest
  ) {
    return authService.getUsersByIds(ids, userRequest);
  }

  @GetMapping("users/{id}")
  public UserResponse getUsersById(@PathVariable("userId") UUID id,
                                   @ModelAttribute UserRequest userRequest,
                                   HttpServletRequest request) {
    Boolean isService = (Boolean) request.getAttribute("isService");
    if (Boolean.TRUE.equals(isService)) {

      log.trace("Request from Internal service");
    } else {
      log.info("Request from User");
    }
    return authService.getUsersById(id, userRequest);
  }
}
