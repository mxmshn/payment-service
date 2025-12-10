package com.iprody.crm.paymentservice.dto.response;

import java.time.OffsetDateTime;

public record ErrorResponse(

        String status,

        String error,

        String message,

        OffsetDateTime timestamp) {
}
