package com.izak.notification_service.notification.service;

import com.izak.notification_service.kafka.kafka_events.Payment;
import com.izak.notification_service.notification.client.AuthClient;
import com.izak.notification_service.notification.client.OrderClient;
import com.izak.notification_service.notification.dtos.BuyerResponse;
import com.izak.notification_service.notification.dtos.OrderResponse;
import com.izak.notification_service.notification.repository.NotificationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final OrderClient orderClient;
  private final AuthClient authClient;

  @Async
  public void sendMailNotification(
        Payment payment
  ) {
    /*
     TODO: Request the order service for the order , use the url request to gateway using feign client
     TODO: Request to the auth service to give me the buyers details, includes the email and phone Number using feign client
     TODO: Send an email to the user on the payment success use the java mailing
     TODO: Generate a receipt/invoice of the product made
    */
    log.info("Received payment event: {}", payment);
    UUID orderId = payment.orderId();
    log.info("Sending the request of the payment OrderId : {}", orderId);

    OrderResponse orderResponse;
    try {
      orderResponse = orderClient.getOrderById(orderId);
      log.info("Order service response for the request Id: {}", orderResponse);
    } catch (FeignException.NotFound e) {
      throw new RuntimeException("Order not found in order service. OrderId =" + orderId);
    }

    UUID buyerId = orderResponse.buyer_id();
    log.info("Buyer ID: {}", buyerId);

    BuyerResponse buyer;

    try {
      buyer = authClient.getBuyerById(buyerId);
      log.info("Buyer response: {}", buyer);
    } catch (FeignException.NotFound e) {
      throw new RuntimeException("Buyer not found in Auth Service. buyerId=" + buyerId);
    }

  }
}
