package com.izak.payment_service.payment.entity;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "payment")
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private UUID orderId;
  @Column(nullable = false)
  private UUID buyerId;

  @Column(nullable = false)
  private String phoneNumber;
  @Column(nullable = false)
  private BigDecimal amount;

  @Column(nullable = false, unique = true)
  private String transactionReference;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method")
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status")
  private PaymentStatus paymentStatus; // PENDING

  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;
}
