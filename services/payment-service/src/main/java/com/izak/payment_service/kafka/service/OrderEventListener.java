package com.izak.payment_service.kafka.service;

import com.izak.payment_service.OrderEvent.service.PaymentIntentService;
import com.izak.payment_service.kafka.dto.OrderEventMessage;
import com.izak.payment_service.kafka.events.OrderEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
  private final PaymentIntentService paymentIntentService;
  // TODO: Add a queue to read the data from the database as a QUEUE
  // private final PaymentQueueService paymentQueueService;

  @KafkaListener(
        topics = "order_created",
        containerFactory = "orderEventKafkaFactory",
        groupId = "payment-ms"
  )
  @Transactional
  public void handleOrderCreated(OrderEventMessage orderEventMessage) {
    OrderEvent orderEvent = orderEventMessage.getData();
    log.info("Order created event received for order {}", orderEvent.getId());

    try {
      paymentIntentService.createPaymentIntent(orderEventMessage);
    } catch (DataIntegrityViolationException e) {
      log.warn("PaymentIntent already exists for order {}", orderEvent.getId());
    }
  }

  @KafkaListener(
        topics = "order_updated",
        containerFactory = "orderEventKafkaFactory",
        groupId = "payment-ms"
  )

  public void handleOrderUpdated(OrderEventMessage orderEventMessage) {
    OrderEvent orderEvent = orderEventMessage.getData();
    log.info("Order updated event received for order {}", orderEvent.getId());
    paymentIntentService.updatePaymentIntent(orderEventMessage);
  }
}
