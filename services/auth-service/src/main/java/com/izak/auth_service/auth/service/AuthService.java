package com.izak.auth_service.auth.service;

import com.izak.auth_service.auth.dto.*;
import com.izak.auth_service.auth.mapper.AuthMapper;
import com.izak.auth_service.configuration.JwtService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final AuthMapper authMapper;

  public AuthResponse register(RegisterRequest registerRequest) {
    User user = userRepository.save(authMapper.register(registerRequest));
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public AuthResponse registerSeller(RegisterRequest registerRequest) {
    User user = userRepository.save(authMapper.registerSeller(registerRequest));
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public AuthResponse registerAdmin(RegisterRequest registerRequest) {
    User user = userRepository.save(authMapper.registerAdmin(registerRequest));
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public AuthResponse authenticate(AuthenticateRequest authenticateRequest) {
    authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                authenticateRequest.email(),
                authenticateRequest.password()
          )
    );
    var user = userRepository.findByEmail(authenticateRequest.email())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public User updateProfile(
        UUID id,
        UpdateUser updateUser
  ) {
    User user = userRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("User not found with id "));

    user.setFirstName(updateUser.firstName());
    user.setLastName(updateUser.lastName());
    user.setDob(updateUser.dob());
    user.setGender(updateUser.gender());
    user.setPhoneNumber(updateUser.phoneNumber());
    return userRepository.save(user);
  }


  public UserResponse getUsersById(UUID id, UserRequest userRequest) {
    User user = userRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("User not found"));
    return new UserResponse(
          user.getId(),
          user.getFirstName(),
          user.getLastName(),
          user.getEmail(),
          user.getPhoneNumber(), user.getDob(), user.getGender(),
          user.getAuth()
    );
  }

  public List<UserResponse> getUsersByIds(List<UUID> ids, UserRequest userRequest) {
    List<User> users;

    if (ids == null || ids.isEmpty()) {
      users = userRepository.findAll();
    } else {
      users = userRepository.findAllById(ids);
    }

    List<User> filteredUsers = users.stream()
          .filter(user -> userRequest.getFilter() == null
                || user.getFirstName().toLowerCase().contains(userRequest.getFilter().toLowerCase()))
          .collect(Collectors.toList());
    return filteredUsers.stream()
          .map(user -> new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getDob(), user.getGender(),user.getAuth())
          .collect(Collectors.toList());
  }
}
