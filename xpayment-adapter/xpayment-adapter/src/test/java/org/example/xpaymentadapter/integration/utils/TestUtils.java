package org.example.xpaymentadapter.integration.utils;

import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;

import java.math.BigDecimal;

import java.util.UUID;

public class TestUtils {

    public static PaymentAdapterRequest getTestPaymentRequest(String paymentId) {
        return  new PaymentAdapterRequest(
                UUID.fromString(paymentId),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                "RUB",
                "nikita1",
                UUID.randomUUID(),
                "n1100@bk.ru"
        );
    }
}
