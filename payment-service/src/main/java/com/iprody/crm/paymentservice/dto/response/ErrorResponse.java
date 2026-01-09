package com.iprody.crm.paymentservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Schema(description = "API Error Model")
public record ErrorResponse(

        @Schema(
                description = "Human-readable error message",
                example = "Payment not found"
        )
        String message,

        @Schema(
                description = "Details error information(optional)",
                example = "Inquiry reference cannot be null"
        )
        List<String> details,

        @Schema(description = "GUID of the requested resource, if applicable")
        UUID guid) {

    public static ErrorResponse of(
            String message,
            String... details) {
        return new ErrorResponse(message, List.of(details), null);
    }

    public static ErrorResponse of(
            String message,
            List<String> details) {
        return new ErrorResponse(message, details, null);
    }

    public static ErrorResponse of(
            String message,
            UUID resourceId) {
        return new ErrorResponse(message, Collections.emptyList(), resourceId);
    }
}
