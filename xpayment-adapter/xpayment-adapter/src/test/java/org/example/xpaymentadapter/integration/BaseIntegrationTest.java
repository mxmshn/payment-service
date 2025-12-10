package org.example.xpaymentadapter.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.xpaymentadapter.integration.config.TestConstants;
import org.example.xpaymentadapter.integration.mocks.PaymentServiceMockServer;
import org.example.xpaymentadapter.pojo.kafka.producer.PaymentAdapterResponse;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Testcontainers
public abstract class BaseIntegrationTest {


    protected static PaymentServiceMockServer mockServer;

    @BeforeAll
    static void beforeAll() {
        mockServer = new PaymentServiceMockServer();
        mockServer.mockCreateCharge();
        mockServer.mockGetChargeSuccess();
    }

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(TestConstants.POSTGRES_16)
            .withDatabaseName(TestConstants.TEST_DB_NAME)
            .withUsername(TestConstants.TEST_DB_USERNAME)
            .withPassword(TestConstants.TEST_DB_PASSWORD);


    @Container
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse(TestConstants.KAFKA_IMAGE_NAME)
    ).withEmbeddedZookeeper();

    @Container
    static final RabbitMQContainer rabbitmq = new RabbitMQContainer(
            DockerImageName.parse(TestConstants.RABBIT_IMAGE_NAME)
    ).withAdminPassword(TestConstants.RABBIT_PASSWORD)
            .withAdminUser(TestConstants.RABBIT_USERNAME);

    static Consumer<String, PaymentAdapterResponse> getKafkaConsumer() {
        Map<String, Object> consumerProps = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, TestConstants.KAFKA_GROUP_ID_CONFIG,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, TestConstants.KAFKA_AUTO_OFFSET_RESET_CONFIG,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, TestConstants.KAFKA_TRUSTED_PACKAGES,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"
        );

        return new DefaultKafkaConsumerFactory<String, PaymentAdapterResponse>(consumerProps).createConsumer();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        registry.add("spring.kafka.consumer.auto-offset-reset", () -> TestConstants.KAFKA_AUTO_OFFSET_RESET_CONFIG);
        registry.add("xpayment.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("xpayment.kafka.topicName", () -> TestConstants.KAFKA_TOPIC_NAME);
        registry.add("xpayment.kafka.consumer-group-id", () -> TestConstants.KAFKA_GROUP_ID_CONFIG);

        registry.add("xpayment.kafka.observation.observation-key",  () -> TestConstants.OBSERVATION_KEY);
        registry.add("xpayment.kafka.observation.observation-name",  () -> TestConstants.OBSERVATION_NAME);

        registry.add("xpayment.api.base-url", mockServer::getBaseUrl);
        registry.add("xpayment.api.x-pay-account", ()-> TestConstants.PAYMENT_API_ACCOUNT);
        registry.add("xpayment.api.token", ()-> TestConstants.PAYMENT_API_TOKEN);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> TestConstants.HIBERNATE_DDL_AUTO);
        registry.add("spring.jpa.defer-datasource-initialization", () -> TestConstants.HIBERNATE_DEFER_DATASOURCE_INITIALIZATION);
        registry.add("spring.sql.init.mode", () -> TestConstants.SQL_INIT_MODE);
    }
}
