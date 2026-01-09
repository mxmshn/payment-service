package com.iprody.crm.paymentservice.service.impl;

import com.iprody.crm.paymentservice.config.AppConfigurationProperties;
import com.iprody.crm.paymentservice.exception.error.ValidationException;
import com.iprody.crm.paymentservice.service.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PageableValidator implements Validator<Pageable> {

    private final AppConfigurationProperties config;

    @Override
    public void validate(Pageable pageable) {
        AppConfigurationProperties.Pagination pageConfig =
                config.getPagination();

        List<String> details = new ArrayList<>();

        if (pageable.getPageNumber() < 0) {
            details.add("Page number must equal or greater than zero");
        }

        if (!pageConfig.getAllowedPageSize().contains(pageable.getPageSize())) {
            details.add(String.format(
                    "Page size '%d' is not allowed. Allowed values: %s",
                    pageable.getPageSize(),
                    pageConfig.getAllowedPageSize()));
        }

        pageable.getSort().forEach(order -> {
            if (!pageConfig.getAllowedSort().contains(order.getProperty())) {
                details.add(String.format(
                        "Sorting by '%s' is not allowed",
                        order.getProperty()));
            }
        });

        if (!details.isEmpty()) {
            throw new ValidationException(
                    "Invalid pagination parameters", details);
        }
    }
}
