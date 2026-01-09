package com.iprody.crm.paymentservice.kafka.producer;

import com.iprody.crm.paymentservice.config.AppConfigurationProperties;
import com.iprody.crm.paymentservice.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapterProducer {

    private final KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;
    private final AppConfigurationProperties properties;

    public CompletableFuture<SendResult<String, PaymentCreatedEvent>>
    send(PaymentCreatedEvent event) {
        return kafkaTemplate.send(
                properties.getTopics().getPaymentExecutionRequest(),
                event.guid().toString(),
                event
        );
    }
}
