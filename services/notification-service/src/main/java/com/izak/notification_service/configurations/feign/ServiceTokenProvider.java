package com.izak.notification_service.configurations.feign;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServiceTokenProvider {
  private final Map<String, String> serviceTokens = Map.of(
        "django-order-service", "DJANGO_ORDER_SERVICE_TOKEN",
        "notification-service", "NOTIFICATION_SERVICE_TOKEN",
        "auth-service", "AUTH_SERVICE_TOKEN"
  );

  // Get token by service name
  public String getToken(String serviceName) {
    return serviceTokens.get(serviceName);
  }
}
