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
    private final Topics topics;

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

    @Getter
    @RequiredArgsConstructor
    public static class Topics {

        private final String paymentStatusUpdated;
        private final String paymentExecutionRequest;
        private final String paymentExecutionResponse;
    }
}
