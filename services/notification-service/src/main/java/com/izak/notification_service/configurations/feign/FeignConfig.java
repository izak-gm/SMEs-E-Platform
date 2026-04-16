package com.izak.notification_service.configurations.feign;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FeignConfig {
  private final ServiceTokenProvider tokenProvider;

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignClientErrorDecoder();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      // Feign client name
      String serviceName = requestTemplate.feignTarget().name();
      log.debug("service Name: {}", serviceName);
      String token = tokenProvider.getToken(serviceName);

      if (token != null) {
        requestTemplate.header("Authorization", "Bearer " + token);
      }
    };
  }
}
