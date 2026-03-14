package com.izak.notification_service.notification.service;

import com.izak.notification_service.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;

  public void sendMailNotification(
        UUID orderId
  ) {
    /*
     TODO: Request the order service for the order , use the url request to gateway
     TODO: Request to the auth service to give me the buyers details, includes the email and phone Number
     TODO: Send an email to the user on the payment success
     TODO: Generate a receipt/invoice of the product made
    */

  }
}
