package org.example.xpaymentadapter.client.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    public static final String TRACE_ID = "traceId";
    public static final String LOG_TEXT_OUTGOING_REQUEST_INFO = "OUTGOING REQUEST [{}] to {}: {} {}";
    public static final String LOG_TEXT_REQUEST_TO_URL_BODY_TRACE = "[{}] Request to {} - URL: {} - Body: {}";
    public static final String LOG_TEXT_RESPONSE_INFO = "RESPONSE [{}] from {}: {} {} - Status: {} ({} ms)";
    public static final String LOG_TEXT_REQUEST_FAILED_ERROR = "REQUEST FAILED [{}] to {}: {} {} - Error: {} ({} ms)";

    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte[] body, @NonNull  ClientHttpRequestExecution execution) throws IOException {
        String traceId = MDC.get(TRACE_ID);
        String serviceName = request.getURI().getHost();

        log.info(LOG_TEXT_OUTGOING_REQUEST_INFO,
                traceId, serviceName, request.getMethod(), request.getURI());

        if (log.isTraceEnabled()) {
            log.trace(LOG_TEXT_REQUEST_TO_URL_BODY_TRACE,
                    traceId, serviceName, request.getURI(),
                    body);
        }

        long startTime = System.currentTimeMillis();
        try {
            ClientHttpResponse response = execution.execute(request, body);

            long duration = System.currentTimeMillis() - startTime;
            log.info(LOG_TEXT_RESPONSE_INFO,
                    traceId, serviceName, request.getMethod(), request.getURI(),
                    response.getStatusCode(), duration);

            return response;
        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error(LOG_TEXT_REQUEST_FAILED_ERROR,
                    traceId, serviceName, request.getMethod(), request.getURI(),
                    e.getMessage(), duration);
            throw e;
        }
    }

}