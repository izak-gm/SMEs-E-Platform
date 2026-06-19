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
      log.debug("Feign target service: {}", serviceName);

      String token = tokenProvider.getToken(serviceName);

      if (token != null) {
        requestTemplate.header("X-API-KEY", token);
        log.debug("API key attached for service: {}", serviceName);
      } else {
        log.warn("No API key found for service: {}", serviceName);
      }
    };
  }
}
