package org.example.xpaymentadapter.scheduler;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.client.XPaymentClient;
import org.example.xpaymentadapter.component.kafka.producer.PaymentServiceKafkaProducer;
import org.example.xpaymentadapter.entity.Payment;
import org.example.xpaymentadapter.mapper.PaymentMapper;
import org.example.xpaymentadapter.pojo.PaymentStatus;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentStatusTracker {
    private final XPaymentClient xPaymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentServiceKafkaProducer producer;
    private final PaymentMapper paymentMapper;

    @Scheduled(fixedRateString = "${app.scheduler.check-payment-status-interval:}")
    public void checkProcessingPayments() {
        List<Payment> processingPayments = paymentRepository.findAll();

        if (processingPayments.isEmpty()) {
            return;
        }

        List<Payment> stillProcessing = processingPayments.stream().filter(payment -> {
            XPaymentApiResponse xPaymentApiResponse = xPaymentClient.getCharge(payment.getPaymentId());
            if (PaymentStatus.PROCESSING.name().equals(xPaymentApiResponse.status())) {
                return true;
            }
            payment.setStatus(xPaymentApiResponse.status());
            producer.notifyPaymentFinalStatus(paymentMapper.toPaymentAdapterResponse(payment));
            paymentRepository.delete(payment);
            return false;
        }).toList();

        log.info("Checked {} payments. Queue size: {}", processingPayments.size(), processingPayments.size());
    }
}