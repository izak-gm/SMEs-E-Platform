package com.izak.payment_service.payment.service;

import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.dto.PaymentResponse;
import com.izak.payment_service.payment.entity.Payment;
import com.izak.payment_service.payment.enums.Method;
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
  private final PaymentIntentRepository paymentIntentRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentMapper mapper;

  public PaymentResponse initiatePayment(PaymentRequest paymentRequest){
    // Find the order for the buyer
    PaymentIntent paymentIntent=paymentIntentRepository.findByOrderId(paymentRequest.orderId()).orElseThrow(()->
          new IllegalStateException(
                "PaymentIntent not found for orderId :" + paymentRequest.orderId()
          )
          );

    // check which type of transaction Type the user want to use
    // Initiate the correct services for the user method of payment
    // TODO :When the payment is initiated then direct the method to use ie Card | Mpesa
    switch (paymentRequest.method()){
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
            "Unsupported payment method: "+ paymentRequest.method()
      );
    }

    // save the Transaction
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

}
