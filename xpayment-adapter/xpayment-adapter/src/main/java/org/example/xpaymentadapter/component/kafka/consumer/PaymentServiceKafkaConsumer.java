package org.example.xpaymentadapter.component.kafka.consumer;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.configuration.properties.KafkaConsumerProperties;
import org.example.xpaymentadapter.pojo.kafka.consumer.PaymentAdapterRequest;
import org.example.xpaymentadapter.service.PaymentProcessorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class PaymentServiceKafkaConsumer {

    public static final String LOG_TEXT_INCOMING_MESSAGE = "Incoming message from Kafka: {}";


    private final PaymentProcessorService paymentProcessorService;
    private final ObservationRegistry observationRegistry;
    private final KafkaConsumerProperties kafkaConsumerProperties;

    @KafkaListener(topics = "${xpayment.kafka.topic-name}", errorHandler = "kafkaErrorHandler")
    public void consumePaymentRequest(PaymentAdapterRequest request) {
        Observation.createNotStarted(kafkaConsumerProperties.observationName(), observationRegistry)
                .lowCardinalityKeyValue(kafkaConsumerProperties.observationKey(), "${xpayment.kafka.topic-name}")
                .observe(() -> {
                    log.info(LOG_TEXT_INCOMING_MESSAGE, request);
                    paymentProcessorService.createPayment(request);
                });
    }
}
