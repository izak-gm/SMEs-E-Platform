package com.izak.notification_service.configurations.feign;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@ToString
public class InternalServiceTokens {

  @Value("${DJANGO_ORDER_SERVICE_TOKEN}")
  private String djangoToken;

  @Value("${NOTIFICATION_SERVICE_TOKEN}")
  private String notificationToken;

  @Value("${AUTH_SERVICE_TOKEN}")
  private String authToken;

}