package com.iprody.crm.paymentservice.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(

        UUID guid,
        UUID inquiryRefId,
        BigDecimal amount,
        String currency,
        String customer,
        UUID order,
        String receiptEmail) {
}
