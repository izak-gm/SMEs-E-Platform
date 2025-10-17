package com.izak.auth_service.auth.service;

import com.izak.auth_service.auth.dto.AuthResponse;
import com.izak.auth_service.auth.dto.AuthenticateRequest;
import com.izak.auth_service.auth.dto.RegisterRequest;
import com.izak.auth_service.auth.dto.UpdateUser;
import com.izak.auth_service.auth.mapper.AuthMapper;
//import com.izak.auth_service.exceptions.UserNotFoundException;
import com.izak.auth_service.configuration.JwtService;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                authenticateRequest.email(),
                authenticateRequest.password()
          )
    );
    var user=userRepository.findByEmail(authenticateRequest.email())
          .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    var jwtToken=jwtService.generateToken(user);
    return AuthResponse.builder()
          .token(jwtToken)
          .build();
  }

  public User updateProfile(
        Long id,
        UpdateUser updateUser
  ){
    User user=userRepository.findById(id)
          .orElseThrow(()->new RuntimeException("User not found with id "));

    user.setFirstName(updateUser.firstName());
    user.setLastName(updateUser.lastName());
    user.setDob(updateUser.dob());
    user.setGender(updateUser.gender());
    user.setPhoneNumber(updateUser.phoneNumber());
    return userRepository.save(user);
  }
}
