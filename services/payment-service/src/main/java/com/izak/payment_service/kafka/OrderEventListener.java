package com.izak.payment_service.kafka;
import com.izak.payment_service.events.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {
  @KafkaListener(
        topics = "order_created_topic",
        containerFactory = "orderEventKafkaFactory"
  )
  public void listenOrderEvents(OrderEvent event) {
    System.out.println("Received Order Event: " + event);
  }
}
