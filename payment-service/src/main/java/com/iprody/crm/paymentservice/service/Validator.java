package com.iprody.crm.paymentservice.service;

public interface Validator<T> {
    void validate(T t);
}
