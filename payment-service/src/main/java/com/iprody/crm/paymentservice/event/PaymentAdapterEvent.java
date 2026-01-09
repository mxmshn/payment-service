package com.iprody.crm.paymentservice.event;

import com.iprody.crm.paymentservice.model.enums.Status;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentAdapterEvent(

        UUID guid,
        UUID inquiryRefId,
        BigDecimal amount,
        String currency,
        UUID transactionRefId,
        Status status) {
}
