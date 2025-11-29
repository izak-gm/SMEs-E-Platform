package com.izak.payment_service.payment.entity;

import com.izak.payment_service.payment.enums.Method;
import com.izak.payment_service.payment.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)

  private UUID id;
  private UUID orderId;
  private UUID buyerId;
  private String phoneNumber;
  private BigDecimal amount;
  private String transactionReference;
  @Enumerated(EnumType.STRING)
  private Method method;
  @Enumerated(EnumType.STRING)
  private Status status;

  @CreatedDate
  private Date createdAt;
  @LastModifiedDate
  private Date updatedAt;
}
