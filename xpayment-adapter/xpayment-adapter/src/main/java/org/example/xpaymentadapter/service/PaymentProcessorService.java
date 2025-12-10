package org.example.xpaymentadapter.service;

import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;

public interface PaymentProcessorService {
    void createPayment(PaymentAdapterRequest paymentAdapterRequest);
}
