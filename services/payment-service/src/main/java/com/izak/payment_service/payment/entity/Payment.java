package com.izak.payment_service.payment.entity;

import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "payments")
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
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus; // PENDING

  @CreationTimestamp
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;
}
