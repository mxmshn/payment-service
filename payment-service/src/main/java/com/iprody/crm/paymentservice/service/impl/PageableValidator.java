package com.iprody.crm.paymentservice.service.impl;

import com.iprody.crm.paymentservice.config.AppConfigurationProperties;
import com.iprody.crm.paymentservice.exception.ValidationException;
import com.iprody.crm.paymentservice.service.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageableValidator implements Validator<Pageable> {

    private final AppConfigurationProperties config;

    @Override
    public void validate(Pageable pageable) {
        var pageConfig = config.getPagination();

        if (pageable.getPageNumber() < 0) {
            throw new ValidationException("Page number must greater than zero");
        }

        if (!pageConfig.getAllowedPageSize().contains(pageable.getPageSize())) {
            throw new ValidationException(
                    String.format(
                            "Page size '%d' is not allowed. Allowed values: %s",
                            pageable.getPageSize(),
                            pageConfig.getAllowedPageSize()));
        }

        pageable.getSort().forEach(order -> {
            if (!pageConfig.getAllowedSort().contains(order.getProperty())) {
                throw new ValidationException(
                        String.format(
                                "Sorting by '%s' is not allowed",
                                order.getProperty())
                );
            }
        });
    }
}
