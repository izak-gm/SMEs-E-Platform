package com.izak.payment_service.callbacks.entity;

import com.izak.payment_service.callbacks.enums.CallbackProvider;
import com.izak.payment_service.callbacks.enums.CallbackStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments_callbacks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsCallbacks {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CallbackProvider callbackProvider; // CARD OR MPESA

  @Column(name = "trans_amount", nullable = false)
  private BigDecimal transAmount;

  @Column(name = "transaction_id", nullable = false, unique = true)
  // MpesaReceiptNumber OR Card reference
  private String transactionId;

  @Column(name = "transaction_reference", nullable = false, unique = true)
  // MpesaTransReference OR Card reference
  private String transactionReference;

  private String phoneNumber; // M-Pesa
  @Column(name = "checkout_request_id", nullable = false, unique = true)
  private String checkoutRequestId;

  @Column(name = "callback_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private CallbackStatus callbackStatus; // PAID

  @Column(name = "paid_at", nullable = false)
  private Instant paidAt;

  @CreationTimestamp
  private Instant createdAt;
}
