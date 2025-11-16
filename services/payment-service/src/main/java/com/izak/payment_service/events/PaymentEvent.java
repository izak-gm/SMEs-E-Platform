package com.izak.payment_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
  private String orderId;
  private BigDecimal amount;
  private String status;
  private String paymentMethod;
}
