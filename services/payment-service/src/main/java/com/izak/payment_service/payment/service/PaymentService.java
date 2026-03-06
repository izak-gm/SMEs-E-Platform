package com.izak.payment_service.payment.service;

import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.enums.PaymentStatus;
import com.izak.payment_service.kafka.events.PaymentEvent;
import com.izak.payment_service.kafka.publish.PaymentProducer;
import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.dto.PaymentResponse;
import com.izak.payment_service.payment.entity.Payment;
import com.izak.payment_service.payment.mapper.PaymentMapper;
import com.izak.payment_service.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {
  private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
  private final PaymentIntentRepository paymentIntentRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentMapper mapper;
  private final PaymentProducer paymentProducer;

  public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
    // Find the order for the buyer
    PaymentIntent paymentIntent = paymentIntentRepository.findByOrderId(paymentRequest.orderId()).orElseThrow(() ->
          new IllegalStateException(
                "PaymentIntent not found for orderId :" + paymentRequest.orderId()
          )
    );

    // check which type of transaction Type the user want to use
    // Initiate the correct services for the user method of payment
    // TODO :When the payment is initiated then direct the method to use ie Card | Mpesa
    switch (paymentRequest.paymentMethod()) {
      case CARD -> {
        // handle card payment
        //TODO: Create a mpesa service that communicates with Mpesa endpoints values (amount,phoneNumber,TransactionReference)
        log.info("Payment Initiated for the Mpesa method");
        // fetch buyers info from Auth Ms ie phoneNumber or have a field that can take a phone Number

      }
      case MPESA -> {
        // TODO: Create a service that communicates with banking api  services
        log.info("Payment Initiated for the Card method");

      }
      default -> throw new IllegalStateException(
            "Unsupported payment method: " + paymentRequest.paymentMethod()
      );
    }

    // save the Transaction
    Payment payment = paymentRepository.save(mapper.makePayment(paymentRequest));

    log.info(String.valueOf(payment));
    BigDecimal roundedAmount = payment.getAmount().setScale(0, RoundingMode.CEILING);

    return PaymentResponse.builder()
          .amount(roundedAmount)
          .transactionReference(payment.getTransactionReference())
          .phoneNumber(payment.getPhoneNumber())
          .order_id(payment.getOrderId())
          .buyer_id(payment.getBuyerId())
          .paymentStatus(payment.getPaymentStatus())
          .paymentMethod(payment.getPaymentMethod())
          .build();
  }

  @Transactional
  public void markMpesaPaymentSuccess(
        String CheckoutRequestId,
        String TransactionID,
        String PhoneNumber,
        String TransAmount,
        String TransactionReference
  ) {
    log.info("M-Pesa SUCCESS | checkout={} receipt={}", CheckoutRequestId, TransactionID);
    // update payment
    Payment payment = paymentRepository.findByTransactionReference(TransactionReference).orElseThrow(() ->
          new IllegalStateException("Payment with transaction reference:" + TransactionReference + " does not exist in the payments.")
    );
    // Idempotency protection (Mpesa may retry callbacks)
    if (payment.getPaymentStatus() == PaymentStatus.PAID) {
      log.warn("Duplicate Mpesa callbacks ignored | Transaction Reference={}", TransactionReference);
    }

    payment.setPaymentStatus(PaymentStatus.PAID);
    paymentRepository.save(payment);

    UUID orderId = payment.getOrderId();
    // update payment_intent
    PaymentIntent paymentIntent = paymentIntentRepository.findByOrderId(orderId).orElseThrow(() ->
          new IllegalStateException("Payment Intent with this orderId do not exist: " + orderId));

    paymentIntent.setStatus(PaymentStatus.SUCCESS);
    paymentIntentRepository.save(paymentIntent);

    // publish Kafka event: payment_completed
    PaymentEvent paymentEvent = new PaymentEvent(
          orderId,
          payment.getAmount(),
          payment.getPaymentStatus().name(),
          payment.getPaymentMethod().name(),
          Instant.now()
    );
    paymentProducer.publishPaymentCompleted(paymentEvent);
  }

  public void markMpesaPaymentFailed(String CheckoutRequestId, String status) {
    log.warn("M-Pesa FAILED | checkout={} status={}", CheckoutRequestId, status);
  }
}
