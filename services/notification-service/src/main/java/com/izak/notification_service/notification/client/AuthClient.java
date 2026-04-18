package com.izak.notification_service.notification.client;

import com.izak.notification_service.configurations.feign.FeignConfig;
import com.izak.notification_service.notification.dtos.BuyerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
      name = "auth-service",
      url = "${services.gateway.url}",
      configuration = FeignConfig.class
)
public interface AuthClient {
  @GetMapping("v1/api/auth/users/{id}")
  BuyerResponse getBuyerById(@PathVariable UUID id);
}
