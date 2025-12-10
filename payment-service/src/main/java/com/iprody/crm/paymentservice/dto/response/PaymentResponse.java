package com.iprody.crm.paymentservice.dto.response;

import com.iprody.crm.paymentservice.model.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(

        UUID guid,

        UUID inquiryRefId,

        BigDecimal amount,

        String currency,

        Status status,

        String note,

        LocalDateTime createdAt) {
}
