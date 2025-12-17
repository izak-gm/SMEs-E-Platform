package com.izak.payment_service.kafka.service;
import com.izak.payment_service.OrderEvent.service.PaymentIntentService;
import com.izak.payment_service.kafka.dto.OrderEventMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    log.info("Received Order Event");

    try{
          paymentIntentService.createPaymentIntent(orderEventMessage);
    } catch (IllegalStateException e) {
      log.warn("Duplicate payment intent ignored: {}", e.getMessage());
    }

    // Async Payment Initiation
    // paymentQueueService.initiatePayment(paymentIntent.getId());

  }
}
