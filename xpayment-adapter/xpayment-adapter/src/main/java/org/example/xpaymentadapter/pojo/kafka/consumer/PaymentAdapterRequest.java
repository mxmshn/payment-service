package org.example.xpaymentadapter.pojo.kafka.consumer;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentAdapterRequest (
    UUID guid,
    UUID inquiryRefId,
    BigDecimal amount,
    String currency,
    String customer,
    UUID order,
    String receiptEmail
) {}