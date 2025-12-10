package com.iprody.crm.paymentservice.validation;

import com.iprody.crm.paymentservice.dto.request.PaymentFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentFilterDatesValidator implements
        ConstraintValidator<ValidPaymentFilterDates, PaymentFilterRequest> {

    @Override
    public boolean isValid(PaymentFilterRequest dto,
                           ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        var createdAt = dto.createdAt();
        var createdFrom = dto.createdFrom();
        var createdTo = dto.createdTo();
        context.disableDefaultConstraintViolation();
        boolean valid = true;

        if (createdAt != null && (createdFrom != null || createdTo != null)) {
            String message = """
                    Cannot use 'createdAt' together with
                    'createdFrom' or 'createdTo'""";

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("createdAt")
                    .addConstraintViolation();
            valid = false;
        }

        if (createdFrom != null && createdTo != null
                && createdFrom.isAfter(createdTo)) {
            String msg = "createdFrom (%s) must be before/equal createdTo (%s)"
                    .formatted(createdFrom, createdTo);

            context.buildConstraintViolationWithTemplate(msg)
                    .addPropertyNode("createdFrom")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }

}
