package org.example.xpaymentadapter.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "xpayment.api")
public record XPaymentClientProperties (
    String baseUrl,
    String xPayAccount,
    String token) {}
