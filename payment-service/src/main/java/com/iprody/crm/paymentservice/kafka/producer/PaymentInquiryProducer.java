package com.iprody.crm.paymentservice.kafka.producer;

import com.iprody.crm.paymentservice.config.AppConfigurationProperties;
import com.iprody.crm.paymentservice.event.PaymentAdapterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PaymentInquiryProducer {

    private final KafkaTemplate<String, PaymentAdapterEvent> kafkaTemplate;
    private final AppConfigurationProperties properties;

    public CompletableFuture<SendResult<String, PaymentAdapterEvent>>
    send(PaymentAdapterEvent event) {
        return kafkaTemplate.send(
                properties.getTopics().getPaymentStatusUpdated(),
                event.guid().toString(),
                event
        );
    }
}
