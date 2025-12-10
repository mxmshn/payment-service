package com.iprody.crm.paymentservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app")
public class AppConfigurationProperties {

    private final Pagination pagination;

    @Getter
    @RequiredArgsConstructor
    public static class Pagination {

        private final List<String> allowedSort;
        private final List<Integer> allowedPageSize;
        private final int defaultPageNumber;
        private final int defaultPageSize;
        private final String defaultFieldSort;
        private final String defaultDirectionSort;
    }
}
