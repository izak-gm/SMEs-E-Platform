package com.izak.payment_service.callbacks.controller;

import com.izak.payment_service.callbacks.dto.MpesaCallbackDTO;
import com.izak.payment_service.callbacks.enums.CallbackStatus;
import com.izak.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment/callbacks/mpesa")
@Slf4j
@RequiredArgsConstructor
public class MpesaCallbackController {
  private final PaymentService paymentService;

  @PostMapping("/stk")
  public ResponseEntity<?> handleStkCallback(@RequestBody MpesaCallbackDTO payload) {
    log.info("Mpesa STK Callback received: {}", payload);
    try {
      if (payload.callbackStatus() == CallbackStatus.PAID) {
        paymentService.markMpesaPaymentSuccess(
              payload.CheckoutRequestId(),
              payload.TransactionID(),
              payload.PhoneNumber(),
              payload.TransactionReference(),
              payload.TransAmount()
        );
      } else {
        paymentService.markMpesaPaymentFailed(payload.CheckoutRequestId(), payload.callbackStatus().name());
      }
      return ResponseEntity.ok("Mpesa Transaction completed and event published successfully");
    } catch (Exception e) {
      log.error("Error processing M-Pesa callback", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
