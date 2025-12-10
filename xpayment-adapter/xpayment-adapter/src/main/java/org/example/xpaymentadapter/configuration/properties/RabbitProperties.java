package org.example.xpaymentadapter.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xpayment.rabbit")
public record RabbitProperties (
        String routingKey) {}

