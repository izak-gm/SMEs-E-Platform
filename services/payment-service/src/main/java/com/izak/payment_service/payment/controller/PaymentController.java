package com.izak.payment_service.payment.controller;

import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.dto.PaymentResponse;
import com.izak.payment_service.payment.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("initiate")
  public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest paymentRequest){
    return ResponseEntity.ok(paymentService.initiatePayment(paymentRequest));
  }

}
