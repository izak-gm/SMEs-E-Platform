package com.izak.payment_service.kafka.events;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderEvent {
  private UUID id;
  private UUID buyer_id;
  private UUID store_id;
  private BigDecimal total_amount;
  private String status;
  private String created_at;
}
