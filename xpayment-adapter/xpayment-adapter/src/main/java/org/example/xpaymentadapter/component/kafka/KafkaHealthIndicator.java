package org.example.xpaymentadapter.component.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaHealthIndicator implements HealthIndicator {

    public static final String PARAM_RESPONSE_TIME_MS = "response.time.ms";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_BROKERS_AVAILABLE = "brokers.available";
    public static final String ERROR_TEXT_CONNECTION_FAILED = "Connection failed";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Health health() {
        Map<String, Object> config = new HashMap<>(
                kafkaTemplate.getProducerFactory().getConfigurationProperties()
        );

        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);
        config.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 1500);

        long startTime = System.currentTimeMillis();

        try (AdminClient admin = AdminClient.create(config)) {
            var nodes = admin.describeCluster().nodes().get(1, TimeUnit.SECONDS);

            long responseTime = System.currentTimeMillis() - startTime;

            return Health.up()
                    .withDetail(PARAM_BROKERS_AVAILABLE, nodes.size())
                    .withDetail(PARAM_RESPONSE_TIME_MS, responseTime)
                    .build();

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            return Health.down()
                    .withDetail(PARAM_ERROR, ERROR_TEXT_CONNECTION_FAILED)
                    .withDetail(PARAM_RESPONSE_TIME_MS, responseTime)
                    .build();
        }
    }
}