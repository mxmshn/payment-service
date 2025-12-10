package org.example.xpaymentadapter.integration;

import org.example.xpaymentadapter.integration.mocks.PaymentServiceMockServer;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MockServerTest {
    public static final String STATUS_PROCESSING = "\"status\": \"PROCESSING\"";
    public static final String CREATE_CHARGE_TEST_REQUEST_BODY = """
            {
              "amount": "100.24",
              "currency": "USD",
              "customer": "nikita1", 
              "order": "123e2000-e89b-12d3-a456-426614174000",
              "receiptEmail": "n1100@bk.ru"
            }
            """;
    public static final String STATUS_SUCCEED = "\"status\": \"SUCCEED\"";

    private PaymentServiceMockServer mockServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        mockServer = new PaymentServiceMockServer();
        restTemplate = new RestTemplate();
        System.out.println("🔧 MockServer URL: " + mockServer.getBaseUrl());
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void shouldTestSimpleMock() {

        mockServer.mockCreateCharge();

        String response = restTemplate.postForObject(
                mockServer.getBaseUrl() + "/charges",
                "{}",
                String.class
        );

        assertThat(response).contains(STATUS_PROCESSING);
    }

    @Test
    void shouldCreateAndGetCharge() {
        UUID transactionId = UUID.randomUUID();
        mockServer.mockCreateCharge();
        mockServer.mockGetChargeSuccess();

        String createResponse = restTemplate.postForObject(
                mockServer.getBaseUrl() + "/charges",
                CREATE_CHARGE_TEST_REQUEST_BODY,
                String.class
        );

        assertThat(createResponse).contains(STATUS_PROCESSING);

        String getResponse = restTemplate.getForObject(
                mockServer.getBaseUrl() + "/charges/" + transactionId,
                String.class
        );

        assertThat(getResponse).contains(STATUS_SUCCEED);
    }
}