package com.izak.payment_service.callbacks.dto;

import com.izak.payment_service.callbacks.enums.CallbackProvider;
import com.izak.payment_service.callbacks.enums.CallbackStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record MpesaCallbackDTO(
      UUID id,
      String transactionReference,
      String transactionId,
      BigDecimal transAmount,
      String phoneNumber,
      String checkoutRequestId,
      CallbackStatus callbackStatus,
      CallbackProvider callbackProvider
) {
}