package com.izak.payment_service.callbacks.service;

import com.izak.payment_service.callbacks.dto.MpesaCallbackDTO;
import com.izak.payment_service.callbacks.mapper.PaymentsCallbackMapper;
import com.izak.payment_service.callbacks.repository.PaymentsCallbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsCallbacksService {
  private final PaymentsCallbackRepository paymentsCallbackRepository;
  private final PaymentsCallbackMapper paymentsCallbackMapper;

  public void savePaymentCallback(MpesaCallbackDTO payload) {
    paymentsCallbackRepository.save(paymentsCallbackMapper.saveCallback(payload));
  }
}
