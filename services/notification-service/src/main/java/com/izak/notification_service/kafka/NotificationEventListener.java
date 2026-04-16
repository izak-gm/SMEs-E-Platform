package com.izak.notification_service.kafka;

import com.izak.notification_service.kafka.kafka_events.Payment;
import com.izak.notification_service.notification.service.NotificationService;
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
        topics = "payment_completed",
        containerFactory = "paymentEventKafkaFactory",
        groupId = "notification-ms"
  )

  public void listenNotificationEvents(Payment event) {
    log.info("Received Notification Event: {}", event);

    try {
      notificationService.sendMailNotification(event);
    } catch (Exception e) {
      // TODO: Try and make this a event driven event for kafka by scheduling it every 5 secs to prevent double or no event submitted
      log.warn("Notification not Processed", e);
      throw new RuntimeException("Notification was not processed. Please debug the system ", e);
    }
  }
}
