package com.izak.payment_service.kafka;
import com.izak.payment_service.kafka.dto.OrderEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventListener {
  @KafkaListener(
        topics = "ORDER_CREATED",
        containerFactory = "orderEventKafkaFactory"
  )
  public void listenOrderEvents(OrderEventMessage event) {
    log.info("Received Order Event");
    log.info("Event type: {}",event.getEvent_type());
    log.info("Order data :{}",event.getData());
  }
}
