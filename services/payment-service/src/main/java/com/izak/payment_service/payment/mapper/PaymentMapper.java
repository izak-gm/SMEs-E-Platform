package com.izak.payment_service.payment.mapper;

import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.enums.PaymentStatus;
import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentMapper {
  private final PaymentIntentRepository paymentIntentRepository;

  public Payment makePayment(PaymentRequest paymentRequest) {
    PaymentIntent paymentIntent = paymentIntentRepository.findByOrderId(paymentRequest.orderId()).orElseThrow(() ->
          new IllegalStateException(
                "PaymentIntent not found for orderId :" + paymentRequest.orderId()
          )
    );

    log.info("Payment Intent found: orderId={}, totalAmount={}, buyerId={}",
          paymentIntent.getOrderId(),
          paymentIntent.getTotal_amount(),
          paymentIntent.getBuyerId());
    BigDecimal order_amount = paymentIntent.getTotal_amount();

    String transactionReference = generateTransactionReference();
    return Payment.builder()
          .id(paymentRequest.id())
          .amount(order_amount)
          // generate the transaction reference here when building the payment request
          .transactionReference(transactionReference)
          .phoneNumber(paymentRequest.phoneNumber())
          .orderId(paymentIntent.getOrderId())
          .buyerId(paymentIntent.getBuyerId())
          .paymentMethod(paymentRequest.paymentMethod())
          .paymentStatus(PaymentStatus.PENDING)
          .build();
  }

  public String generateTransactionReference() {
    final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    final String prefix = "TXN-BIHB-";
    final Integer length = 16;

    String randomTransactionReference = randomString(characters, length);
    return prefix + randomTransactionReference;
  }

  public String randomString(String characters, int length) {
    Random random = new Random();
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < length; i++) {
      stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
    }
    return stringBuilder.toString();
  }
}
