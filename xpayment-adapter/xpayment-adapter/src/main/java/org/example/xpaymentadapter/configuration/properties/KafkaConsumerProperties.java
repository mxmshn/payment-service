package org.example.xpaymentadapter.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xpayment.kafka.observation")
public record KafkaConsumerProperties(
        String observationKey,
        String observationName
) {}
