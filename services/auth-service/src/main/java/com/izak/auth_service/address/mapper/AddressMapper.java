package com.izak.auth_service.address.mapper;

import com.izak.auth_service.address.dto.AddressRequest;
import com.izak.auth_service.address.entity.Address;
import com.izak.auth_service.user.entity.User;
import com.izak.auth_service.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressMapper {
  private UserRepository userRepository;
  public Address myAddress(AddressRequest addressRequest){
    Long userId = addressRequest.user().getId();
    User user = userRepository.findById(userId)
          .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

    return Address.builder()
          .user(user)
          .town(addressRequest.town())
          .city(addressRequest.city())
          .county(addressRequest.county())
          .postalCode(addressRequest.postalCode())
          .build();
  }
}
