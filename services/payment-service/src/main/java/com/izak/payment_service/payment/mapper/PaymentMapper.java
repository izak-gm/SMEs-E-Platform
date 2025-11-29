package com.izak.payment_service.payment.mapper;

import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.entity.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentMapper {
  public Payment makePayment(PaymentRequest paymentRequest){
    return Payment.builder()
          .id(paymentRequest.id())
          .amount(paymentRequest.amount())
          // TODO : generate the transaction reference here when building the payment request
          .transactionReference(paymentRequest.transactionReference())
          .phoneNumber(paymentRequest.phoneNumber())
          .orderId(paymentRequest.orderId())
          .buyerId(paymentRequest.buyerId())
          .method(paymentRequest.method())
          .status(paymentRequest.status())
          .build();
  }

  public String generateTransactionReference(){
    String characters ="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    Integer length= 13;

    return new String(characters);
  }
}
