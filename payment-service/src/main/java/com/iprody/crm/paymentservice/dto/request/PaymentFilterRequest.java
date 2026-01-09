package com.iprody.crm.paymentservice.dto.request;

import com.iprody.crm.paymentservice.model.enums.Status;
import com.iprody.crm.paymentservice.validation.ValidPaymentFilterDates;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Filter parameters")
@ValidPaymentFilterDates
public record PaymentFilterRequest(

        @Schema(description = "GUID of the payment resource",
                example = "6F9619FF-8B86-D011-B42D-00CF4FC964FF")
        UUID guid,

        @Schema(description = "Inquiry ref id",
                example = "123e4567-e89b-12d3-a456-478914174000")
        UUID inquiryRefId,

        @Schema(description = "status of payment",
                example = "RECEIVED")
        Status status,

        @Schema(description = "Exact creation timestamp",
                example = "2025-12-15T16:11:30")
        @PastOrPresent(message = "createdAt must be in the past or present")
        LocalDateTime createdAt,

        @Schema(description = "Start date for creation filter",
                example = "2025-10-10T00:00:00")
        @PastOrPresent(message = "createdFrom must be in the past or present")
        LocalDateTime createdFrom,

        @Schema(description = "End date for creation filter",
                example = "2026-10-10T00:00:00")
        LocalDateTime createdTo) {
}
