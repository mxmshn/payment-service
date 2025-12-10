package org.example.xpaymentadapter.unit;

import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.entity.Payment;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.rabbit.RabbitPaymentMessageService;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.example.xpaymentadapter.scheduler.DeadLetterQueueTracker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueTrackerTest {

    public static final XPaymentApiRequest MESSAGE = new XPaymentApiRequest("100", "USD", "customer1",
            UUID.randomUUID(), "test@email.com", Map.of());

    public static final XPaymentApiRequest FAILED_MESSAGE = new XPaymentApiRequest("200", "EUR", "customer2",
            UUID.randomUUID(), "test2@email.com", Map.of());

    @Mock
    private RabbitPaymentMessageService rabbitPaymentMessageService;

    @Mock
    private XPaymentClient xPaymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private DeadLetterQueueTracker deadLetterQueueTracker;

    @Test
    void processDlqMessages_NoMessages_NothingHappens() {
        when(rabbitPaymentMessageService.receiveOneMessage()).thenReturn(null);

        deadLetterQueueTracker.processDlqMessages();

        verify(rabbitPaymentMessageService).receiveOneMessage();
        verifyNoInteractions(xPaymentClient, paymentRepository, paymentMapper);
        verify(rabbitPaymentMessageService, never()).send(any());
    }

    @Test
    void processDlqMessages_SuccessfulPayment_SavesToDb() {
        XPaymentApiRequest message = MESSAGE;

        XPaymentApiResponse successResponse = getSuccessResponse(message);

        Payment paymentEntity = new Payment();

        when(rabbitPaymentMessageService.receiveOneMessage())
                .thenReturn(message)
                .thenReturn(null);
        when(xPaymentClient.createChargeWithoutRetry(message)).thenReturn(successResponse);
        when(paymentMapper.toPaymentEntity(successResponse, message)).thenReturn(paymentEntity);

        deadLetterQueueTracker.processDlqMessages();

        verify(paymentRepository).save(paymentEntity);
        verify(rabbitPaymentMessageService, never()).send(any());
    }

    @Test
    void processDlqMessages_FailedPayment_ReturnsToQueue() {
        XPaymentApiRequest message = MESSAGE;

        when(rabbitPaymentMessageService.receiveOneMessage())
                .thenReturn(message)
                .thenReturn(null);
        when(xPaymentClient.createChargeWithoutRetry(message)).thenReturn(null);

        deadLetterQueueTracker.processDlqMessages();

        verify(paymentRepository, never()).save(any());
        verify(rabbitPaymentMessageService).send(message);
    }

    @Test
    void processDlqMessages_Exception_ReturnsToQueue() {
        XPaymentApiRequest message = MESSAGE;

        when(rabbitPaymentMessageService.receiveOneMessage())
                .thenReturn(message)
                .thenReturn(null);
        when(xPaymentClient.createChargeWithoutRetry(message))
                .thenThrow(new RuntimeException("Network error"));

        deadLetterQueueTracker.processDlqMessages();

        verify(paymentRepository, never()).save(any());
        verify(rabbitPaymentMessageService).send(message);
    }

    @Test
    void processDlqMessages_MultipleMessages_MixedResults() {
        XPaymentApiRequest successMessage = MESSAGE;
        XPaymentApiRequest failedMessage = FAILED_MESSAGE;

        XPaymentApiResponse successResponse = getSuccessResponse(MESSAGE);

        Payment paymentEntity = new Payment();

        when(rabbitPaymentMessageService.receiveOneMessage())
                .thenReturn(successMessage)
                .thenReturn(failedMessage)
                .thenReturn(null);
        when(xPaymentClient.createChargeWithoutRetry(successMessage)).thenReturn(successResponse);
        when(xPaymentClient.createChargeWithoutRetry(failedMessage)).thenReturn(null);
        when(paymentMapper.toPaymentEntity(successResponse, successMessage)).thenReturn(paymentEntity);

        deadLetterQueueTracker.processDlqMessages();

        verify(paymentRepository).save(paymentEntity);
        verify(rabbitPaymentMessageService).send(failedMessage);
    }

    private static XPaymentApiResponse getSuccessResponse(XPaymentApiRequest message) {
        return new XPaymentApiResponse(
                UUID.randomUUID(), "100", "USD", "100", null, null,
                "customer1", message.order(), "test@email.com", "SUCCEEDED", null
        );
    }
}
