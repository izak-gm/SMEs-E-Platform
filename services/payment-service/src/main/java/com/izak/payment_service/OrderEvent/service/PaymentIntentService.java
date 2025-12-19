package com.izak.payment_service.OrderEvent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.enums.PaymentStatus;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.kafka.dto.OrderEventMessage;
import com.izak.payment_service.kafka.events.OrderEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentIntentService {
  private final PaymentIntentRepository paymentIntentRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public PaymentIntent createPaymentIntent(OrderEventMessage event){
    OrderEvent  order= event.getData();

    // Get the id of the order from the event Kafka
    UUID orderId =order.getId();

    // Idempotency
    paymentIntentRepository.findByOrderId(orderId).ifPresent(intent ->{
      throw new IllegalStateException("PaymentIntent already exists for order "+orderId);
    });

    // Create a metadata snapshot
    // Build metadata snapshot (JSONB-safe)
    Map<String, Object> metadata = buildMetadata(event);

    // Persist Payment Intent
    PaymentIntent paymentIntent=new PaymentIntent();
    paymentIntent.setOrderId(orderId);
    paymentIntent.setBuyerId(order.getBuyer_id());
    paymentIntent.setStoreId(order.getStore_id());
    paymentIntent.setTotal_amount(order.getTotal_amount());
    paymentIntent.setCurrency("KES");
    paymentIntent.setStatus(PaymentStatus.INITIATED);
    paymentIntent.setMetadata(metadata);
    paymentIntent.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));

    return paymentIntentRepository.save(paymentIntent);
  }

  /**
    *Builds metadata map that is SAFE to persist as JSONB.
    * No raw DTOs are stored directly.
  */
  private Map<String,Object> buildMetadata(OrderEventMessage orderEventMessage){
    Map<String,Object> metadata=new HashMap<>();
    metadata.put("event_type",orderEventMessage.getEvent_type());
    metadata.put("order", orderEventMessage.getData());
    metadata.put("received_at",Instant.now().toString());

    return metadata;

    /*try{
      // Serialize nested DTO explicitly
      metadata.put(
            "order",objectMapper.writeValueAsString(orderEventMessage.getData())
      );

    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize order event metadata",e);
    }
  */
  }
}
