package com.izak.payment_service.OrderEvent.service;

import com.izak.payment_service.kafka.events.OrderEvent;
import com.izak.payment_service.OrderEvent.mapper.OrderMapper;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCacheService {
  private final PaymentIntentRepository paymentIntentRepository;
  private final OrderMapper mapper;

  public OrderEvent saveOrderCache(){
    OrderEvent orderEvent= paymentIntentRepository.save
  }
}
