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
          .TransAmount(callbackDTO.TransAmount())
          .TransactionID(callbackDTO.TransactionID())
          .TransactionReference(callbackDTO.TransactionReference())
          .PhoneNumber(callbackDTO.PhoneNumber())
          .CheckoutRequestId(callbackDTO.CheckoutRequestId())
          .callbackStatus(callbackDTO.callbackStatus())
          .paidAt(Instant.now())
          .createdAt(Instant.now())
          .build();
  }
}
