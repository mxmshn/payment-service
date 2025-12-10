package org.example.xpaymentadapter.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xpayment.kafka")
public record KafkaProperties(
        String bootstrapServers,
        String consumerGroupId,
        String autoOffsetReset,
        String trustedPackages,
        String valueDefaultType
) {}
