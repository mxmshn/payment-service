package org.example.xpaymentadapter.integration.config;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class TestConstants {
    public static final String MOCKSERVER_LOG_LEVEL = "MOCKSERVER_LOG_LEVEL";

    public static final String FORMAT = "http://%s:%d";

    public static final String RABBIT_IMAGE_NAME = "rabbitmq:3-management";
    public static final String RABBIT_PASSWORD = "guest";
    public static final String RABBIT_USERNAME = "guest";

    public static final String KAFKA_GROUP_ID_CONFIG = "test-payment-group";
    public static final String KAFKA_AUTO_OFFSET_RESET_CONFIG = "earliest";
    public static final String KAFKA_TOPIC_NAME = "payment-requests";
    public static final String KAFKA_RESPONSE_TOPIC_NAME = "payment-response";
    public static final String KAFKA_IMAGE_NAME = "confluentinc/cp-kafka:7.4.4";
    public static final String KAFKA_TRUSTED_PACKAGES = "*";

    public static final String OBSERVATION_KEY = "topic";
    public static final String OBSERVATION_NAME = "kafka.consume";

    public static final String HIBERNATE_DDL_AUTO = "create-drop";
    public static final String HIBERNATE_DEFER_DATASOURCE_INITIALIZATION = "true";
    public static final String SQL_INIT_MODE = "always";
    public static final String RABBIT_QUEUE_NAME = "test-dlq";

    public static String MOCKSERVER_IMAGE = "mockserver/mockserver:5.15.0";
    public static final int MOCKSERVER_PORT = 1080;

    public static final String POSTGRES_16 = "postgres:16";
    public static final String TEST_DB_USERNAME = "test";
    public static final String TEST_DB_PASSWORD = "test";
    public static final String TEST_DB_NAME = "test_db";

    public static final String PAYMENT_API_ACCOUNT = "TestAccount";
    public static final String PAYMENT_API_TOKEN = "TestToken=";
}
