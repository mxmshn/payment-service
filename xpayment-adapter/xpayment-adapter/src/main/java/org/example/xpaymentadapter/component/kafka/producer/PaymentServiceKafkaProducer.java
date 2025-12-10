package org.example.xpaymentadapter.component.kafka.producer;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class PaymentServiceKafkaProducer {

    public static final String LOG_TEXT_OUTGOING_MESSAGE_TO_PAYMENT_SERVICE_BY_KAFKA = "Outgoing message to Payment Service by Kafka: {}";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void notifyPaymentFinalStatus(PaymentAdapterResponse response) {
        kafkaTemplate.send("payment-response", response.guid().toString(), response);
        log.info(LOG_TEXT_OUTGOING_MESSAGE_TO_PAYMENT_SERVICE_BY_KAFKA, response);
    }

}
