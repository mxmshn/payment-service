package org.example.xpaymentadapter.client.interceptor;

import lombok.RequiredArgsConstructor;
import org.example.xpaymentadapter.configuration.properties.XPaymentClientProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class XPaymentAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String X_PAY_ACCOUNT = "X-Pay-Account";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";

    private final XPaymentClientProperties properties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();

        headers.set(X_PAY_ACCOUNT, properties.xPayAccount());
        headers.set(AUTHORIZATION, BASIC + properties.token());

        return execution.execute(request, body);
    }
}
