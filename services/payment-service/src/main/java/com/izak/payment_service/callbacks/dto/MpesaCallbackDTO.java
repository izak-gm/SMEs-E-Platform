package com.izak.payment_service.callbacks.dto;

import com.izak.payment_service.callbacks.enums.CallbackProvider;
import com.izak.payment_service.callbacks.enums.CallbackStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record MpesaCallbackDTO(
      UUID id,
      String TransactionReference,
      String TransactionID,
      BigDecimal TransAmount,
      String PhoneNumber,
      String CheckoutRequestId,
      CallbackStatus callbackStatus,
      CallbackProvider callbackProvider
) {
}
