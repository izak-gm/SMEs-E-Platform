package com.izak.payment_service.callbacks.entity;

import com.izak.payment_service.callbacks.enums.CallbackProvider;
import com.izak.payment_service.callbacks.enums.CallbackStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
      name = "payments_callbacks",
      indexes = {
            @Index(name = "idx_payment_order", columnList = "orderId"),
            @Index(name = "idx_payment_tx", columnList = "transactionReference")
      }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsCallbacks {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CallbackProvider callbackProvider; // CARD OR MPESA

  @Column(nullable = false)
  private BigDecimal TransAmount;

  @Column(nullable = false, unique = true)
  // MpesaReceiptNumber OR Card reference
  private String TransactionID;

  @Column(nullable = false, unique = true)
  // MpesaTransReference OR Card reference
  private String TransactionReference;

  private String PhoneNumber; // M-Pesa
  @Column(nullable = false, unique = true)
  private String CheckoutRequestId;

  @Column(nullable = false)
  private CallbackStatus callbackStatus; // PAID

  @Column(nullable = false)
  private Instant paidAt;

  @CreationTimestamp
  private Instant createdAt;
}
