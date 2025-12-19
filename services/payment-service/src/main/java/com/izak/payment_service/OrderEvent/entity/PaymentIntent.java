package com.izak.payment_service.OrderEvent.entity;

import com.izak.payment_service.OrderEvent.converter.JsonMapConverter;
import com.izak.payment_service.OrderEvent.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
      name = "payment_intents",
      uniqueConstraints = @UniqueConstraint(columnNames = "order_id")
)public class PaymentIntent {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "buyer_id", nullable = false)
  private UUID buyerId;

  @Column(name = "store_id", nullable = false)
  private UUID storeId;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total_amount;

  @Column(nullable = false)
  private String currency = "KES";

  @Enumerated(EnumType.STRING)
  private PaymentStatus status = PaymentStatus.INITIATED;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @CreationTimestamp
  private Instant createdAt;
}
