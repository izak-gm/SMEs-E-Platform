package com.izak.notification_service.notification.client;

import com.izak.notification_service.configurations.feign.FeignConfig;
import com.izak.notification_service.notification.dtos.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
      name = "gateway",
      url = "${services.gateway.url",
      configuration = FeignConfig.class
)
public interface OrderClient {
  @GetMapping("v1/api/bizhub-orderline/orders/{id}")
  OrderResponse getOrder(@PathVariable UUID id);
}
