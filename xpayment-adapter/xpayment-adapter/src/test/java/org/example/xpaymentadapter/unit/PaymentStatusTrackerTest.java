package org.example.xpaymentadapter.unit;

import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.component.kafka.producer.PaymentServiceKafkaProducer;
import org.example.xpaymentadapter.entity.Payment;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.example.xpaymentadapter.scheduler.PaymentStatusTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentStatusTrackerTest {

    @Mock
    private XPaymentClient xPaymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentServiceKafkaProducer producer;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentStatusTracker paymentStatusTracker;

    @Test
    void checkProcessingPayments_EmptyList_DoesNothing() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        paymentStatusTracker.checkProcessingPayments();

        assertAll(
                () -> verify(paymentRepository).findAll(),
                () -> verifyNoInteractions(xPaymentClient, producer, paymentMapper)
        );
    }

    @Test
    void checkProcessingPayments_PaymentStillProcessing_NotifiesAndDeletes() {
        UUID paymentId1 = UUID.randomUUID();
        UUID paymentId2 = UUID.randomUUID();

        Payment processingPayment = new Payment(paymentId1, UUID.randomUUID(),
                UUID.randomUUID(), BigDecimal.TEN, "USD", "PROCESSING");

        Payment completedPayment = new Payment(paymentId2, UUID.randomUUID(),
                UUID.randomUUID(), BigDecimal.TEN, "USD", "PROCESSING");

        XPaymentApiResponse stillProcessingResponse = new XPaymentApiResponse(
                paymentId1, "10", "USD", "10", null, null,
                null, null, null, "PROCESSING", null
        );

        XPaymentApiResponse completedResponse = new XPaymentApiResponse(
                paymentId2, "10", "USD", "10", null, null,
                null, null, null, "SUCCEEDED", null
        );

        PaymentAdapterResponse adapterResponse = new PaymentAdapterResponse(
                completedPayment.getGuid(), completedPayment.getInquiryRefId(),
                "10", "USD", completedPayment.getPaymentId(), "SUCCEEDED"
        );

        when(paymentRepository.findAll()).thenReturn(List.of(processingPayment, completedPayment));
        when(xPaymentClient.getCharge(paymentId1)).thenReturn(stillProcessingResponse);
        when(xPaymentClient.getCharge(paymentId2)).thenReturn(completedResponse);
        when(paymentMapper.toPaymentAdapterResponse(completedPayment)).thenReturn(adapterResponse);

        paymentStatusTracker.checkProcessingPayments();

        verify(producer).notifyPaymentFinalStatus(adapterResponse);
        verify(paymentRepository).delete(completedPayment);
        assertThat(completedPayment.getStatus()).isEqualTo("SUCCEEDED");

        verify(paymentRepository, never()).delete(processingPayment);
    }
}
