package org.example.xpaymentadapter.integration.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.example.xpaymentadapter.integration.config.TestConstants;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class PaymentServiceMockServer {


    private final GenericContainer<?> mockServerContainer;
    private final MockServerClient mockServerClient;
    private final ObjectMapper objectMapper;

    @Getter
    private final String baseUrl;

    public PaymentServiceMockServer() {
        this.objectMapper = new ObjectMapper();
        this.mockServerContainer = createMockServerContainer();
        this.mockServerContainer.start();

        this.baseUrl = buildBaseUrl();
        this.mockServerClient = createMockServerClient();
    }

    private GenericContainer<?> createMockServerContainer() {
        return new GenericContainer<>(DockerImageName.parse(TestConstants.MOCKSERVER_IMAGE))
                .withExposedPorts(TestConstants.MOCKSERVER_PORT)
                .withEnv(TestConstants.MOCKSERVER_LOG_LEVEL, "INFO")
                .waitingFor(Wait.forHttp("/")
                        .forStatusCode(404)
                        .withStartupTimeout(java.time.Duration.ofSeconds(30)));
    }

    private String buildBaseUrl() {
        return String.format(TestConstants.FORMAT,
                mockServerContainer.getHost(),
                mockServerContainer.getFirstMappedPort());
    }

    private MockServerClient createMockServerClient() {
        return new MockServerClient(
                mockServerContainer.getHost(),
                mockServerContainer.getFirstMappedPort()
        );
    }

    public void stop() {
        mockServerClient.stop();
        mockServerContainer.stop();
    }

    public void mockCreateCharge() {
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/charges"))
                .respond(request -> {
                    try {
                        String body = request.getBody().toString();
                        JsonNode jsonNode = objectMapper.readTree(body);

                        BigDecimal amount = getBigDecimal(jsonNode, "amount", new BigDecimal("100.00"));
                        String currency = getText(jsonNode, "currency", "RUB");
                        String customer = getText(jsonNode, "customer", "Test Customer");
                        UUID order = getUUID(jsonNode, "order", UUID.randomUUID());
                        String receiptEmail = getText(jsonNode, "receiptEmail", "test@example.com");

                        return response()
                                .withStatusCode(201)
                                .withHeader("Content-Type", "application/json")
                                .withBody(createChargeResponse(amount, currency, customer, order, receiptEmail));
                    } catch (Exception e) {
                        return response()
                                .withStatusCode(400)
                                .withBody("{\"error\": \"Invalid request body\"}");
                    }
                });
    }

    public void mockGetChargeSuccess() {
        mockServerClient
                .when(request()
                        .withMethod("GET")
                        .withPath("/charges/.+"))
                .respond(request -> {
                    try {
                        String path = request.getPath().getValue();
                        UUID chargeId = extractChargeIdFromPath(path);

                        return response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(createChargeResponseForGet(chargeId));
                    } catch (Exception e) {
                        return response()
                                .withStatusCode(404)
                                .withBody("{\"error\": \"Charge not found\"}");
                    }
                });
    }

    private String createChargeResponse(BigDecimal amount, String currency, String customer,
                                        UUID order, String receiptEmail) {
        UUID transactionId = UUID.randomUUID();
        String currentTime = Instant.now().toString();

        return String.format("""
            {
              "id": "%s",
              "amount": %s,
              "currency": "%s",
              "amountReceived": %s,
              "createdAt": "%s",
              "chargedAt": "%s",
              "customer": "%s",
              "order": "%s",
              "receiptEmail": "%s",
              "status": "PROCESSING"
            }
            """, transactionId,
                amount,
                currency,
                amount,
                currentTime,
                currentTime,
                customer,
                order,
                receiptEmail);
    }

    private String createChargeResponseForGet(UUID chargeId) {
        return createChargeResponseWithStatus(chargeId, "SUCCEEDED");
    }

    private String createChargeResponseWithStatus(UUID chargeId, String status) {
        String currentTime = Instant.now().toString();
        BigDecimal amount = new BigDecimal("100.00");

        return String.format("""
            {
              "id": "%s",
              "amount": %s,
              "currency": "RUB",
              "amountReceived": %s,
              "createdAt": "%s",
              "chargedAt": "%s",
              "customer": "Test Customer",
              "order": "%s",
              "receiptEmail": "test@example.com",
              "status": "%s"
            }
            """, chargeId,
                amount,
                amount,
                currentTime,
                currentTime,
                UUID.randomUUID(),
                status);
    }

    private BigDecimal getBigDecimal(JsonNode node, String field, BigDecimal defaultValue) {
        if (node.has(field) && node.get(field).isNumber()) {
            return node.get(field).decimalValue();
        } else if (node.has(field) && node.get(field).isTextual()) {
            try {
                return new BigDecimal(node.get(field).asText());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String getText(JsonNode node, String field, String defaultValue) {
        return node.has(field) && node.get(field).isTextual()
                ? node.get(field).asText()
                : defaultValue;
    }

    private UUID getUUID(JsonNode node, String field, UUID defaultValue) {
        if (node.has(field) && node.get(field).isTextual()) {
            try {
                return UUID.fromString(node.get(field).asText());
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private UUID extractChargeIdFromPath(String path) {
        String[] parts = path.split("/");
        String chargeIdStr = parts[parts.length - 1];
        return UUID.fromString(chargeIdStr);
    }

}