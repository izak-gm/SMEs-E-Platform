package com.izak.payment_service.callbacks.dto;

import com.izak.payment_service.callbacks.enums.CallbackStatus;

import java.math.BigDecimal;

public record MpesaCallbackDTO(
      String TransactionReference,
      String TransactionID,
      BigDecimal TransAmount,
      String PhoneNumber,
      String CheckoutRequestId,
      CallbackStatus callbackStatus
) {
}
