package com.izak.payment_service.payment.mapper;

import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.entity.Payment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class PaymentMapper {
  public Payment makePayment(PaymentRequest paymentRequest){
    String transactionReference=paymentRequest.transactionReference() != null
          ? paymentRequest.transactionReference() : generateTransactionReference();

    return Payment.builder()
          .id(paymentRequest.id())
          .amount(paymentRequest.amount())
          // TODO : generate the transaction reference here when building the payment request
          .transactionReference(transactionReference)
          .phoneNumber(paymentRequest.phoneNumber())
          .orderId(paymentRequest.orderId())
          .buyerId(paymentRequest.buyerId())
          .method(paymentRequest.method())
          .status(paymentRequest.status())
          .build();
  }

  public String generateTransactionReference(){
    final String characters ="ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    final String prefix="TXN-BIHB-";
    final Integer length=16;

    String randomTransactionReference=randomString(characters,length);
    return prefix+randomTransactionReference;
  }

  public String randomString(String characters, int length){
    Random random=new Random();
    StringBuilder stringBuilder=new StringBuilder();

    for(int i=0;i<length; i++){
      stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
    }
    return stringBuilder.toString();
  }
}
