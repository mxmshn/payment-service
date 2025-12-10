package org.example.xpaymentadapter.pojo.paymentClient;

import java.util.Map;
import java.util.UUID;

public record XPaymentApiRequest (
     String amount,
     String currency,
     String customer,
     UUID order,
     String receiptEmail,
     Map<String, String> metadata
    ) {}

