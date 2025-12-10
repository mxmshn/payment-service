package com.iprody.crm.paymentservice.dto.request;

import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.validation.ValidPaymentFilterDates;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.UUID;

@ValidPaymentFilterDates
public record PaymentFilterRequest(

        UUID guid,

        UUID inquiryRefId,

        Status status,

        @PastOrPresent(message = "createdAt must be in the past or present")
        LocalDateTime createdAt,

        @PastOrPresent(message = "createdFrom must be in the past or present")
        LocalDateTime createdFrom,

        @PastOrPresent(message = "createdTo must be in the past or present")
        LocalDateTime createdTo) {

    public PaymentFilterRequest {
    }
}
