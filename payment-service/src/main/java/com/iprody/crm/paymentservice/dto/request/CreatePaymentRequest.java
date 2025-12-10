package com.iprody.crm.paymentservice.dto.request;

import com.iprody.crm.paymentservice.constant.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(

        @NotNull(message = "Inquiry reference cannot be null")
        UUID inquiryRefId,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Currency cannot be blank")
        @Size(min = ValidationConstants.CURRENCY_CODE_LENGTH,
                max = ValidationConstants.CURRENCY_CODE_LENGTH,
                message = "Currency must be 3 characters long")
        String currency,

        String note) {
}
