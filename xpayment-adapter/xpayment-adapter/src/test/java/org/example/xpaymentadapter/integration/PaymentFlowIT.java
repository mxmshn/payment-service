package org.example.xpaymentadapter.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.example.xpaymentadapter.integration.config.TestConstants;
import org.example.xpaymentadapter.integration.utils.TestUtils;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class PaymentFlowIT extends BaseIntegrationTest {

    public static final String STATUS_SUCCEEDED = "SUCCEEDED";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldSendPaymentResponseToKafka() {
        kafka.getBootstrapServers();

        UUID paymentId = UUID.randomUUID();
        var paymentRequest = TestUtils.getTestPaymentRequest(paymentId.toString());

        Consumer<String, PaymentAdapterResponse> consumer = getKafkaConsumer();

        try (consumer) {
            consumer.subscribe(java.util.Collections.singletonList(TestConstants.KAFKA_RESPONSE_TOPIC_NAME));
            kafkaTemplate.send(TestConstants.KAFKA_TOPIC_NAME, paymentRequest);

            await().atMost(180, SECONDS).untilAsserted(() -> {
                ConsumerRecords<String, PaymentAdapterResponse> records = consumer.poll(Duration.ofSeconds(1));

                assertThat(records).isNotEmpty();
                assertThat(records.count()).isGreaterThan(0);

                records.forEach(record -> {
                    PaymentAdapterResponse response = record.value();
                    assertThat(response).isNotNull();
                    assertThat(response.guid()).isEqualTo(paymentId);
                    assertThat(response.status()).isEqualTo(STATUS_SUCCEEDED);
                });
            });
        }
    }

}
