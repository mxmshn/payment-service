package com.iprody.crm.paymentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
@RequiredArgsConstructor
public class PaginationConfiguration {

    private final AppConfigurationProperties appProperties;

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizePageable() {
        var config = appProperties.getPagination();
        var directionSort = config.getDefaultDirectionSort().equals("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return resolver -> {
            resolver.setFallbackPageable(
                    PageRequest.of(
                            config.getDefaultPageNumber(),
                            config.getDefaultPageSize(),
                            Sort.by(directionSort, config.getDefaultFieldSort())
                    )
            );
        };
    }
}
