package com.izak.auth_service.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class InternalServiceTokens {

  @Value("${DJANGO_ORDER_SERVICE_TOKEN}")
  private String djangoToken;

  @Value("${NOTIFICATION_SERVICE_TOKEN}")
  private String notificationToken;

  @Value("${AUTH_SERVICE_TOKEN}")
  private String authToken;

}