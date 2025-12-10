package org.example.xpaymentadapter.pojo.paymentClient;

import java.util.UUID;

public record XPaymentApiResponse(
        UUID id,
        String amount,
        String currency,
        String amountReceived,
        String createdAt,
        String chargedAt,
        String customer,
        UUID order,
        String receiptEmail,
        String status,
        Object metadata
) {
    public static XPaymentApiResponse failed(UUID order) {
        return new XPaymentApiResponse(null, null, null, null, null, null, null, order, null, "FAILED", null);
    }
}
