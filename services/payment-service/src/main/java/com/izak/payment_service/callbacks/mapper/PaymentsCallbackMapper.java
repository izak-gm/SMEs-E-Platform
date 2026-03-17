package com.izak.payment_service.callbacks.mapper;

import com.izak.payment_service.callbacks.dto.MpesaCallbackDTO;
import com.izak.payment_service.callbacks.entity.PaymentsCallbacks;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentsCallbackMapper {
  public PaymentsCallbacks saveCallback(MpesaCallbackDTO callbackDTO) {
    return PaymentsCallbacks.builder()
          .id(callbackDTO.id())
          .callbackProvider(callbackDTO.callbackProvider())
          .transAmount(callbackDTO.transAmount())
          .transactionID(callbackDTO.transactionID())
          .transactionReference(callbackDTO.transactionReference())
          .phoneNumber(callbackDTO.phoneNumber())
          .checkoutRequestId(callbackDTO.checkoutRequestId())
          .callbackStatus(callbackDTO.callbackStatus())
          .paidAt(Instant.now())
          .createdAt(Instant.now())
          .build();
  }
}
