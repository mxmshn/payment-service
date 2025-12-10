package com.iprody.crm.paymentservice.dto.request;

import com.iprody.crm.paymentservice.constant.ValidationConstants;
import com.iprody.crm.paymentservice.model.enums.Status;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentUpdateRequest(

        BigDecimal amount,

        @Size(min = ValidationConstants.CURRENCY_CODE_LENGTH,
                max = ValidationConstants.CURRENCY_CODE_LENGTH,
                message = "Currency must be 3 characters long")
        String currency,

        Status status,

        String note) {
}
