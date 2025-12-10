package org.example.xpaymentadapter.pojo.kafka.producer;

import java.util.UUID;

public record PaymentAdapterResponse (
     UUID guid,
     UUID inquiryRefId,
     String amount,
     String currency,
     UUID transactionRefId,
     String status
) {}
