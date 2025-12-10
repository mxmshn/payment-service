package com.iprody.crm.paymentservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentFilterDatesValidator.class)
public @interface ValidPaymentFilterDates {
    String message() default "Invalid payment date filter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
