package org.example.xpaymentadapter.unit;

import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.entity.Payment;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.example.xpaymentadapter.service.PaymentProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import java.util.Map;
import java.math.BigDecimal;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorServiceImplTest {

    @Mock
    private XPaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentProcessorServiceImpl paymentProcessorService;

    private PaymentAdapterRequest testRequest;
    private XPaymentApiRequest testApiRequest;
    private XPaymentApiResponse processingResponse;
    private XPaymentApiResponse successResponse;
    private Payment testPaymentEntity;

    @BeforeEach
    void setUp() {

        testRequest = new PaymentAdapterRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("100.24"), "USD", "nikita1",
                UUID.randomUUID(), "n1100@bk.ru"
        );

        testApiRequest = new XPaymentApiRequest(
                "100.24", "USD", "nikita1",
                testRequest.order(), "n1100@bk.ru",
                Map.of("inquiryRefId", testRequest.inquiryRefId().toString(),
                        "paymentServiceGuid", testRequest.guid().toString())
        );

        successResponse = new XPaymentApiResponse(
                UUID.randomUUID(), "100.24", "USD", "100.24",
                "2024-01-01", "2024-01-01", "nikita1",
                testRequest.order(), "n1100@bk.ru", "SUCCEEDED", null
        );

        processingResponse = new XPaymentApiResponse(
                UUID.randomUUID(), "100.24", "USD", "100.24",
                "2024-01-01", "2024-01-01", "nikita1",
                testRequest.order(), "n1100@bk.ru", "PROCESSING", null
        );

        testPaymentEntity = new Payment(
                successResponse.id(), testRequest.guid(),
                testRequest.inquiryRefId(), new BigDecimal("100.24"),
                "USD", "SUCCEEDED"
        );
    }

    @Test
    void createPayment_Success_SavesToRepository() {
        when(paymentMapper.toXPaymentApiRequest(testRequest)).thenReturn(testApiRequest);
        when(paymentClient.createCharge(testApiRequest)).thenReturn(processingResponse);
        when(paymentMapper.toPaymentEntity(processingResponse, testRequest)).thenReturn(testPaymentEntity);

        paymentProcessorService.createPayment(testRequest);

        verify(paymentMapper).toXPaymentApiRequest(testRequest);
        verify(paymentClient).createCharge(testApiRequest);
        verify(paymentMapper).toPaymentEntity(processingResponse, testRequest);
        verify(paymentRepository).save(testPaymentEntity);
    }

    @Test
    void createPayment_ResponseIsNull_DoesNotSave() {
        when(paymentMapper.toXPaymentApiRequest(testRequest)).thenReturn(testApiRequest);
        when(paymentClient.createCharge(testApiRequest)).thenReturn(null);

        paymentProcessorService.createPayment(testRequest);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_ResponseIdIsNull_DoesNotSave() {
        XPaymentApiResponse responseWithoutId = new XPaymentApiResponse(
                null, "100.24", "USD", "100.24",
                "2024-01-01", "2024-01-01", "nikita1",
                testRequest.order(), "n1100@bk.ru", "SUCCESS", null
        );

        when(paymentMapper.toXPaymentApiRequest(testRequest)).thenReturn(testApiRequest);
        when(paymentClient.createCharge(testApiRequest)).thenReturn(responseWithoutId);

        paymentProcessorService.createPayment(testRequest);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_ClientThrowsException_ExceptionPropagates() {
        when(paymentMapper.toXPaymentApiRequest(testRequest)).thenReturn(testApiRequest);
        when(paymentClient.createCharge(testApiRequest))
                .thenThrow(new RuntimeException("Network error"));

        assertThrows(RuntimeException.class, () -> {
            paymentProcessorService.createPayment(testRequest);
        });

        verify(paymentRepository, never()).save(any());
    }
}
