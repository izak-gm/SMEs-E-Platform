package com.izak.payment_service.kafka.events;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderEvent {
  private String id;
  private String buyer_id;
  private String store_id;
  private BigDecimal total_amount;
  private String status;
  private String created_at;


}
