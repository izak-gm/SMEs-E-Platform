package com.izak.payment_service.payment.service;

import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.dto.PaymentResponse;
import com.izak.payment_service.payment.entity.Payment;
import com.izak.payment_service.payment.mapper.PaymentMapper;
import com.izak.payment_service.payment.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {
  private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
  private final PaymentRepository paymentRepository;
  private final PaymentMapper mapper;

  public PaymentResponse initiatePayment(PaymentRequest paymentRequest){
    Payment payment=paymentRepository.save(mapper.makePayment(paymentRequest));

    log.info(String.valueOf(payment));
    return PaymentResponse.builder()
          .amount(payment.getAmount())
          .transactionReference(payment.getTransactionReference())
          .phoneNumber(payment.getPhoneNumber())
          .order_id(payment.getOrderId())
          .buyer_id(payment.getBuyerId())
          .status(payment.getStatus())
          .method(payment.getMethod())
          .build();
  }

  // TODO :When the payment is initiated then direct the method to use ie Card | Mpesa
  // TODO :Once the response is given after the initiatingPayment method now direct the user to the correct payment method



}
