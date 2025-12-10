package org.example.xpaymentadapter.configuration;

import lombok.AllArgsConstructor;
import org.example.xpaymentadapter.configuration.properties.XPaymentClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
@AllArgsConstructor
public class RestClientConfig {

    private final XPaymentClientProperties properties;
    private final List<ClientHttpRequestInterceptor> interceptorList;

    @Bean
    public RestClient xPaymentRestClient() {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestInterceptors(list -> {
                    list.addAll(interceptorList);
                })
                .build();
    }
}
