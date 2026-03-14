package com.izak.payment_service.kafka.publish;

import com.izak.payment_service.kafka.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
  private static final String TOPIC = "payment_completed";
  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

  public void publishPaymentCompleted(PaymentEvent event) {
    log.info("Publishing payment_completed event for orderId={}", event.orderId());

    kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
  }
}
