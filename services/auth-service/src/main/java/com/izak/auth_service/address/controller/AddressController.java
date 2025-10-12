package com.izak.auth_service.address.controller;

import com.izak.auth_service.address.dto.AddressRequest;
import com.izak.auth_service.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class AddressController {
  private final AddressService addressService;

  @PostMapping("/location")
  public ResponseEntity<AddressRequest> location(@Valid @RequestBody AddressRequest addressRequest) {
    AddressRequest savedAddress = addressService.location(addressRequest);
    return ResponseEntity.ok(savedAddress);
  }
}
