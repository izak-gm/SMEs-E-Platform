package com.izak.notification_service.kafka;

import com.izak.notification_service.kafka_events.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventListener {
  @KafkaListener(
        topics = "notification-topic",
        containerFactory = "notificationEventKafkaFactory"
  )
  public void listenNotificationEvents(Notification event) {
    System.out.println("Received Notification Event: " + event);
  }
}
