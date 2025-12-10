package org.example.xpaymentadapter.integration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import org.apache.kafka.clients.consumer.Consumer;
import org.example.xpaymentadapter.integration.config.TestConstants;
import org.example.xpaymentadapter.integration.utils.TestUtils;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.UUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class DLQTest extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void shouldSendPaymentResponseToKafka() throws Exception {
        mockServer.stop();

        Connection connection = createRabbitConnection(
                rabbitmq.getHost(),
                rabbitmq.getAmqpPort(),
                rabbitmq.getAdminUsername(),
                rabbitmq.getAdminPassword()
        );
        Channel channel = connection.createChannel();

        UUID paymentId = UUID.randomUUID();
        var paymentRequest = TestUtils.getTestPaymentRequest(paymentId.toString());

        String dlqQueueName = TestConstants.RABBIT_QUEUE_NAME;

        channel.queueDeclare(dlqQueueName, true, false, false, null);
        channel.queuePurge(dlqQueueName);

        Consumer<String, PaymentAdapterResponse> consumer = getKafkaConsumer();

        try (consumer) {
            consumer.subscribe(java.util.Collections.singletonList(TestConstants.KAFKA_RESPONSE_TOPIC_NAME));
            kafkaTemplate.send(TestConstants.KAFKA_TOPIC_NAME, paymentRequest);

            channel.queuePurge(dlqQueueName);

            await().atMost(180, SECONDS).untilAsserted(() -> {
                GetResponse response = channel.basicGet(dlqQueueName, true);
                assertThat(response).isNotNull();
            });
        }
    }

    public static Connection createRabbitConnection(String host, int port, String username, String password) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory.newConnection();
    }

}

