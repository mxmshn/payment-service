package com.iprody.crm.paymentservice.exception.error;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final UUID resourceId;

    public ResourceNotFoundException(String message, UUID guid) {
        super(message);
        this.resourceId = guid;
    }
}
