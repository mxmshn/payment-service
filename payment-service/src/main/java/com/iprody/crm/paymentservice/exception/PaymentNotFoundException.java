package com.iprody.crm.paymentservice.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    private static final String MESSAGE_PATTERN =
            "Payment not found with GUID: %s";

    public PaymentNotFoundException(UUID guid) {
        super(String.format(MESSAGE_PATTERN, guid));
    }
}
