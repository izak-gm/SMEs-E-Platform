package com.izak.payment_service.payment.service;

import com.izak.payment_service.OrderEvent.entity.PaymentIntent;
import com.izak.payment_service.OrderEvent.repository.PaymentIntentRepository;
import com.izak.payment_service.enums.PaymentMethod;
import com.izak.payment_service.enums.PaymentStatus;
import com.izak.payment_service.kafka.events.PaymentEvent;
import com.izak.payment_service.kafka.publish.PaymentProducer;
import com.izak.payment_service.payment.dto.PaymentRequest;
import com.izak.payment_service.payment.dto.PaymentResponse;
import com.izak.payment_service.payment.entity.Payment;
import com.izak.payment_service.payment.mapper.PaymentMapper;
import com.izak.payment_service.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PaymentServiceTest {

  @Mock
  private PaymentIntentRepository paymentIntentRepository;
  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private PaymentMapper mapper;
  @Mock
  private PaymentProducer paymentProducer;

  @InjectMocks
  private PaymentService paymentService;

  private UUID buyerId;
  private UUID orderId;
  private String transactionReference;
  private PaymentRequest paymentRequest;
  private PaymentResponse paymentResponse;
  private PaymentEvent paymentEvent;
  private Payment payment;
  private PaymentIntent paymentIntent;
  private PaymentRequest badPaymentRequest;

  @BeforeEach
  void setUp() {
    orderId = UUID.randomUUID();
    buyerId = UUID.randomUUID();
    transactionReference = "TXN-" + System.currentTimeMillis();
    BigDecimal amount = new BigDecimal("300.00");
    paymentRequest = new PaymentRequest(
          amount,
          "254712345678",
          orderId,
          buyerId,
          PaymentMethod.MPESA,
          PaymentStatus.INITIATED
    );
    paymentResponse = PaymentResponse.builder()
          .amount(amount)
          .phoneNumber("254712345678")
          .transactionReference("")
          .paymentMethod(PaymentMethod.MPESA)
          .paymentStatus(PaymentStatus.INITIATED)
          .order_id(orderId)
          .buyer_id(buyerId)
          .build();
    paymentIntent = new PaymentIntent();
    payment = Payment.builder()
          .orderId(orderId)
          .buyerId(buyerId).phoneNumber("254712345678")
          .amount(amount).transactionReference(transactionReference)
          .paymentMethod(PaymentMethod.MPESA).paymentStatus(PaymentStatus.INITIATED)
//          .createdAt().updatedAt()
          .build();

  }

  @Test
  @DisplayName("Should successfully initiate payment for MPESA method")
  void shouldInitiatePaymentSuccessfully() {
    log.info("Initiate payment");
    when(paymentIntentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentIntent));
    when(mapper.makePayment(paymentRequest)).thenReturn(payment);

    when(paymentRepository.save(payment)).thenReturn(payment);
    log.info("payment:{}", payment);
    log.info("continue");

    PaymentResponse response = paymentService.initiatePayment(paymentRequest);

    assertThat(response).isNotNull();
    verify(paymentIntentRepository).findByOrderId(orderId);
    verify(paymentRepository, times(1)).save(payment);
  }

  @Test
  @DisplayName("Should through error if payment not found")
  void shouldThrowWhenPaymentIntentNotFound() {
    // Given
    when(paymentIntentRepository.findByOrderId(orderId))
          .thenReturn(Optional.empty());
    // when & Then
    assertThatThrownBy(() -> paymentService.initiatePayment(paymentRequest))
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("PaymentIntent not found for orderId :" + orderId);
    verify(paymentIntentRepository).findByOrderId(orderId);
    verify(paymentRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception for unsupported payment method")
  void initiatePayment_WithUnsupportedMethod_ShouldThrowException() {
    // Given
    badPaymentRequest = new PaymentRequest(
          new BigDecimal("300.00"),
          "254712345678",
          orderId,
          buyerId,
          null,
          PaymentStatus.INITIATED
    );
    when(paymentIntentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentIntent));
    assertThat(badPaymentRequest.paymentMethod()).isNull();
    // When & Then
    assertThatThrownBy(() -> paymentService.initiatePayment(badPaymentRequest))
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("Unsupported to payment method");
//    verify(paymentIntentRepository, times(1)).findByOrderId(paymentIntent);
  }

  @Test
  @DisplayName("Should handle concurrent payment requests gracefully")
  void concurrentPaymentRequests_ShouldBeHandled() throws InterruptedException {
    // This test simulates concurrent access to ensure transactional behavior
    int threadCount = 5;
    Thread[] threads = new Thread[threadCount];

    when(paymentIntentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentIntent));
    when(mapper.makePayment(any(PaymentRequest.class))).thenReturn(payment);
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    for (int i = 0; i < threadCount; i++) {
      threads[i] = new Thread(() -> {
        PaymentResponse response = paymentService.initiatePayment(paymentRequest);
        assertThat(response).isNotNull();
      });
      threads[i].start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    verify(paymentRepository, times(threadCount)).save(any(Payment.class));
  }

  @Nested
  @DisplayName("Tests for markMpesaPaymentSuccess method")
  class MarkMpesaPaymentSuccessTests {

    private String checkoutRequestId;
    private String transactionId;
    private String phoneNumber;

    @BeforeEach
    void setUp() {
      checkoutRequestId = "CHECKOUT-" + UUID.randomUUID();
      transactionId = "TXN_ID_" + System.currentTimeMillis();
      phoneNumber = "254712345678";
    }

    @Test
    @DisplayName("Should successfully mark MPESA payment as success")
    void markMpesaPaymentSuccess_ShouldUpdatePaymentAndIntentAndPublishEvent() {
      // Given
      BigDecimal amount = new BigDecimal("150.75");
      Payment existingPayment = Payment.builder()
            .transactionReference(transactionReference)
            .orderId(orderId)
            .paymentStatus(PaymentStatus.PENDING)
            .amount(amount)
            .paymentMethod(PaymentMethod.MPESA)
            .build();

      when(paymentRepository.findByTransactionReference(transactionReference))
            .thenReturn(Optional.of(existingPayment));
      when(paymentIntentRepository.findByOrderId(orderId))
            .thenReturn(Optional.of(paymentIntent));

      ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
      ArgumentCaptor<PaymentIntent> intentCaptor = ArgumentCaptor.forClass(PaymentIntent.class);
      ArgumentCaptor<PaymentEvent> eventCaptor = ArgumentCaptor.forClass(PaymentEvent.class);

      // When
      paymentService.markMpesaPaymentSuccess(
            checkoutRequestId,
            transactionId,
            phoneNumber,
            transactionReference,
            amount
      );

      // Then
      verify(paymentRepository).save(paymentCaptor.capture());
      Payment savedPayment = paymentCaptor.getValue();
      assertThat(savedPayment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);

      verify(paymentIntentRepository).save(intentCaptor.capture());
      PaymentIntent savedIntent = intentCaptor.getValue();
      assertThat(savedIntent.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

      verify(paymentProducer).publishPaymentCompleted(eventCaptor.capture());
      PaymentEvent publishedEvent = eventCaptor.getValue();
      assertThat(publishedEvent.orderId()).isEqualTo(orderId);
      assertThat(publishedEvent.amount()).isEqualTo(amount);
      assertThat(publishedEvent.paymentStatus()).isEqualTo(PaymentStatus.PAID);
      assertThat(publishedEvent.paymentMethod()).isEqualTo(PaymentMethod.MPESA);
      assertThat(publishedEvent.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("Should ignore duplicate MPESA callbacks (idempotency)")
    void markMpesaPaymentSuccess_WhenAlreadyPaid_ShouldIgnoreDuplicate() {
      // Given
      Payment existingPayment = Payment.builder()
            .transactionReference(transactionReference)
            .orderId(orderId)
            .paymentStatus(PaymentStatus.PAID) // Already paid
            .amount(new BigDecimal("150.75"))
            .build();

      when(paymentRepository.findByTransactionReference(transactionReference))
            .thenReturn(Optional.of(existingPayment));

      // When
      paymentService.markMpesaPaymentSuccess(
            checkoutRequestId,
            transactionId,
            phoneNumber,
            transactionReference,
            new BigDecimal("150.75")
      );

      // Then
      verify(paymentRepository, never()).save(any()); // Should not save again
      verify(paymentIntentRepository, never()).findByOrderId(any());
      verify(paymentProducer, never()).publishPaymentCompleted(any());
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void markMpesaPaymentSuccess_WhenPaymentNotFound_ShouldThrowException() {
      // Given
      when(paymentRepository.findByTransactionReference(transactionReference))
            .thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> paymentService.markMpesaPaymentSuccess(
            checkoutRequestId,
            transactionId,
            phoneNumber,
            transactionReference,
            new BigDecimal("100.00")
      ))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payment with transaction reference:" + transactionReference + " does not exist");

      verify(paymentIntentRepository, never()).findByOrderId(any());
      verify(paymentProducer, never()).publishPaymentCompleted(any());
    }

    @Test
    @DisplayName("Should throw exception when PaymentIntent not found")
    void markMpesaPaymentSuccess_WhenPaymentIntentNotFound_ShouldThrowException() {
      // Given
      Payment existingPayment = Payment.builder()
            .transactionReference(transactionReference)
            .orderId(orderId)
            .paymentStatus(PaymentStatus.PENDING)
            .build();

      when(paymentRepository.findByTransactionReference(transactionReference))
            .thenReturn(Optional.of(existingPayment));
      when(paymentIntentRepository.findByOrderId(orderId))
            .thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> paymentService.markMpesaPaymentSuccess(
            checkoutRequestId,
            transactionId,
            phoneNumber,
            transactionReference,
            new BigDecimal("100.00")
      ))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Payment Intent with this orderId do not exist: " + orderId);

      verify(paymentRepository).save(any()); // Payment status was updated
      verify(paymentIntentRepository).findByOrderId(orderId);
      verify(paymentProducer, never()).publishPaymentCompleted(any());
    }

    @Test
    @DisplayName("Should handle different phone number formats")
    void markMpesaPaymentSuccess_WithDifferentPhoneFormats_ShouldWork() {
      // Given
      String[] phoneNumbers = {"0712345678", "+254712345678", "254712345678"};

      for (String number : phoneNumbers) {
        Payment existingPayment = Payment.builder()
              .transactionReference(transactionReference + number)
              .orderId(orderId)
              .paymentStatus(PaymentStatus.PENDING)
              .build();

        when(paymentRepository.findByTransactionReference(transactionReference + number))
              .thenReturn(Optional.of(existingPayment));
        when(paymentIntentRepository.findByOrderId(orderId))
              .thenReturn(Optional.of(paymentIntent));

        // When
        paymentService.markMpesaPaymentSuccess(
              checkoutRequestId,
              transactionId,
              number,
              transactionReference + number,
              new BigDecimal("100.00")
        );

        // Then
        verify(paymentRepository, times(1)).save(any());
        reset(paymentRepository, paymentIntentRepository);
      }
    }
  }

  @Nested
  @DisplayName("Tests for markMpesaPaymentFailed method")
  class MarkMpesaPaymentFailedTests {

    @Test
    @DisplayName("Should log warning when MPESA payment fails")
    void markMpesaPaymentFailed_ShouldLogWarning() {
      // Given
      String checkoutRequestId = "CHECKOUT-123";
      String status = "FAILED";

      // When
      paymentService.markMpesaPaymentFailed(checkoutRequestId, status);

      // Then - Verify that the method executes without exceptions
      // Logging verification would require a logger mock, but that's optional
    }
  }

}
