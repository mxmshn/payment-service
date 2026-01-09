package com.iprody.crm.paymentservice.exception.error;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final List<String> errorDetails;

    public ValidationException(String message, List<String> details) {
        super(message);
        this.errorDetails = details;
    }
}
