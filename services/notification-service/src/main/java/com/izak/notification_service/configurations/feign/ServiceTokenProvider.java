package com.izak.notification_service.configurations.feign;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceTokenProvider {
  private final InternalServiceTokens tokens;
  private Map<String, String> serviceTokens;

  @PostConstruct
  public void init() {
    serviceTokens = Map.of(
          "django-order-service", tokens.getDjangoToken(),
          "notification-service", tokens.getNotificationToken(),
          "auth-service", tokens.getAuthToken()
    );
  }

  // Get token by service name
  public String getToken(String serviceName) {
    return serviceTokens.get(serviceName);
  }
}
