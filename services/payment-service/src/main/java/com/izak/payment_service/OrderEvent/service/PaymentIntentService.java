package com.izak.payment_service.OrderEvent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.enums.PaymentStatus;
import com.izak.payment_service.kafka.dto.OrderEventMessage;
import com.izak.payment_service.kafka.events.OrderEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentService {

  private final PaymentIntentRepository paymentIntentRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public PaymentIntent createPaymentIntent(OrderEventMessage event) {

    OrderEvent order = event.getData();
    UUID orderId = order.getId();

    PaymentIntent intent = new PaymentIntent();
    intent.setOrderId(orderId);
    intent.setBuyerId(order.getBuyer_id());
    intent.setStoreId(order.getStore_id());
    intent.setTotal_amount(order.getTotal_amount());
    intent.setCurrency("KES");
    intent.setStatus(PaymentStatus.INITIATED);
    intent.setMetadata(buildMetadata(event));
    intent.setExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES));

    try {
      return paymentIntentRepository.save(intent);
    } catch (DataIntegrityViolationException e) {
      throw new IllegalStateException("PaymentIntent already exists for order " + orderId);
    }
  }

  @Transactional
  public void updatePaymentIntent(OrderEventMessage event) {

    OrderEvent order = event.getData();
    UUID orderId = order.getId();

    PaymentIntent intent = paymentIntentRepository
          .findByOrderId(orderId)
          .orElseThrow(() ->
                new IllegalStateException("PaymentIntent not found for order " + orderId)
          );

    intent.setTotal_amount(order.getTotal_amount());
    intent.setMetadata(buildMetadata(event));

    paymentIntentRepository.save(intent);
  }

  private Map<String, Object> buildMetadata(OrderEventMessage event) {
    try {
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("event_type", event.getEvent_type());
      metadata.put("order", objectMapper.writeValueAsString(event.getData()));
      metadata.put("received_at", Instant.now().toString());
      return metadata;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize metadata", e);
    }
  }
}

