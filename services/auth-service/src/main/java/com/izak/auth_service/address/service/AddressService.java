package com.izak.auth_service.address.service;

import com.izak.auth_service.address.dto.AddressRequest;
import com.izak.auth_service.address.entity.Address;
import com.izak.auth_service.address.mapper.AddressMapper;
import com.izak.auth_service.address.repository.AddressRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AddressService {
  private final AddressRepository addressRepository;
  private final AddressMapper addressMapper;

  public AddressRequest location(AddressRequest addressRequest) {
    Address savedAddress=addressRepository.save(addressMapper.myAddress(addressRequest));

    return AddressRequest.builder()
          .user(savedAddress.getUser())
          .town(savedAddress.getTown())
          .city(savedAddress.getCity())
          .county(savedAddress.getCounty())
          .postalCode(savedAddress.getPostalCode())
          .build();
  }
}
