package com.izak.notification_service.kafka;

import com.izak.notification_service.kafka.kafka_events.Payment;
import com.izak.notification_service.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
  private final NotificationService notificationService;

  @KafkaListener(
        topics = "payment-completed",
        containerFactory = "notificationEventKafkaFactory",
        groupId = "notification-ms"
  )
  @Transactional
  public void listenNotificationEvents(Payment event) {
    log.info("Received Notification Event: {}", event);

    try {
      notificationService.sendMailNotification(event.orderId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
