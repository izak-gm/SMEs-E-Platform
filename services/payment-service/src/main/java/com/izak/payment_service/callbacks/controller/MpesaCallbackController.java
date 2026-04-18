package com.izak.payment_service.callbacks.controller;

import com.izak.payment_service.callbacks.dto.MpesaCallbackDTO;
import com.izak.payment_service.callbacks.enums.CallbackStatus;
import com.izak.payment_service.callbacks.service.PaymentsCallbacksService;
import com.izak.payment_service.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/payment/callbacks/mpesa")
@Slf4j
@RequiredArgsConstructor
public class MpesaCallbackController {
  private final PaymentService paymentService;
  private final PaymentsCallbacksService paymentsCallbacksService;

  @PostMapping("/stk")
  @Transactional
  public ResponseEntity<?> handleStkCallback(@RequestBody MpesaCallbackDTO payload) {
    log.info("Mpesa STK Callback received: {}", payload);

    // save the callbacks
    paymentsCallbacksService.savePaymentCallback(payload);
    try {
      if (payload.callbackStatus() == CallbackStatus.PAID) {
        paymentService.markMpesaPaymentSuccess(
              payload.checkoutRequestId(),
              payload.transactionId(),
              payload.phoneNumber(),
              payload.transactionReference(),
              payload.transAmount()
        );
      } else {
        paymentService.markMpesaPaymentFailed(payload.checkoutRequestId(), payload.callbackStatus().name());
      }
      return ResponseEntity.ok("Mpesa Transaction completed and event published successfully");
    } catch (Exception e) {
      log.error("Error processing M-Pesa callback", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
