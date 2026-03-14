package com.izak.notification_service.configurations.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {
  private final ErrorDecoder defaultDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    switch (response.status()) {
      case 404:
        return new RuntimeException("Resource not found:" + methodKey);
      case 500:
        return new RuntimeException("Internal server error in " + methodKey);
      default:
        return defaultDecoder.decode(methodKey, response);
    }
  }
}
