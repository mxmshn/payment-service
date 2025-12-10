package org.example.xpaymentadapter.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.xpaymentadapter.configuration.properties.XPaymentClientProperties;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiRequest;
import org.example.xpaymentadapter.pojo.paymentClient.XPaymentApiResponse;
import org.example.xpaymentadapter.rabbit.RabbitPaymentMessageService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
public class XPaymentClientImpl implements XPaymentClient {

    private final RestClient restClient;
    private final RabbitPaymentMessageService rabbitPaymentMessageService;
    private final XPaymentClientProperties properties;

    @Override
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 6000))
    public XPaymentApiResponse createCharge(XPaymentApiRequest request) {
        return restClient.post()
                .uri(properties.baseUrl() + "/charges")
                .body(request)
                .retrieve()
                .body(XPaymentApiResponse.class);
    }

    @Override
    public XPaymentApiResponse createChargeWithoutRetry(XPaymentApiRequest request) {
        return restClient.post()
                .uri(properties.baseUrl() + "/charges")
                .body(request)
                .retrieve()
                .body(XPaymentApiResponse.class);
    }

    @Override
    public XPaymentApiResponse getCharge(UUID chargeId) {
        return restClient.get()
                .uri(properties.baseUrl() + "/charges/" + chargeId)
                .retrieve()
                .body(XPaymentApiResponse.class);
    }

    @Override
    @Recover
    public XPaymentApiResponse createChargeFallback(Exception e, XPaymentApiRequest request) {
        log.error("All 3 retries failed for payment: {} Last error: {}", request.order(), e.getMessage());

        try {
            rabbitPaymentMessageService.send(request);
            log.info("Message sent to DLQ: {}", request.order());

        } catch (Exception ex) {
            log.error("Failed to send to DLQ: {}", ex.getMessage());
            ex.printStackTrace();
        }

        return XPaymentApiResponse.failed(request.order());
    }
}
