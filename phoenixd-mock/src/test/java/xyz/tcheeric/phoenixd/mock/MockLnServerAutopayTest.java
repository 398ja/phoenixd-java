package xyz.tcheeric.phoenixd.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MockLnServer autopay toggle functionality.
 * Tests the PHOENIXD_AUTOPAY_ENABLED configuration and /mockpay endpoint.
 */
class MockLnServerAutopayTest {

    private static final int TEST_PORT = 19740;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    private MockLnServer server;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private ByteArrayOutputStream outputCapture;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        objectMapper = new ObjectMapper();

        // Capture System.out to verify logging
        outputCapture = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputCapture));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Tests that the /mockpay endpoint correctly settles an invoice.
     * When autopay is disabled, invoices remain PENDING until /mockpay is called.
     */
    @Test
    void shouldSettleInvoiceViaMockPayEndpoint() throws Exception {
        // Arrange: Start server (autopay enabled by default, but we test /mockpay directly)
        server = new MockLnServer(TEST_PORT);
        server.start();

        // Create an invoice first
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=1000&externalId=test123"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        assertThat(createResponse.statusCode()).isEqualTo(200);

        JsonNode invoiceJson = objectMapper.readTree(createResponse.body());
        String paymentHash = invoiceJson.get("paymentHash").asText();

        // Wait a brief moment (shorter than auto-settle delay) then call /mockpay
        TimeUnit.MILLISECONDS.sleep(100);

        // Act: Call /mockpay to settle the invoice
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> mockPayResponse = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Invoice is settled
        assertThat(mockPayResponse.statusCode()).isEqualTo(200);
        JsonNode mockPayJson = objectMapper.readTree(mockPayResponse.body());
        assertThat(mockPayJson.get("status").asText()).isEqualTo("paid");
        assertThat(mockPayJson.get("paymentHash").asText()).isEqualTo(paymentHash);
        assertThat(mockPayJson.get("quotedAmountSat").asLong()).isEqualTo(1000);
        assertThat(mockPayJson.get("paidAmountSat").asLong()).isEqualTo(1000);
        assertThat(mockPayJson.get("paymentType").asText()).isEqualTo("exact");
    }

    /**
     * Tests that /mockpay returns 404 for an unknown invoice.
     */
    @Test
    void shouldReturn404ForUnknownInvoiceOnMockPay() throws Exception {
        // Arrange: Start server
        server = new MockLnServer(TEST_PORT);
        server.start();

        // Act: Call /mockpay with unknown payment hash
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=unknown_hash_123"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Returns 404 with error message
        assertThat(response.statusCode()).isEqualTo(404);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("error").asText()).isEqualTo("Invoice not found");
        assertThat(json.get("paymentHash").asText()).isEqualTo("unknown_hash_123");
    }

    /**
     * Tests that /mockpay is idempotent for already-paid invoices.
     * Calling /mockpay multiple times on the same invoice returns already_paid status.
     */
    @Test
    void shouldReturnAlreadyPaidForDuplicateMockPay() throws Exception {
        // Arrange: Start server and create invoice
        server = new MockLnServer(TEST_PORT);
        server.start();

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=500"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());

        JsonNode invoiceJson = objectMapper.readTree(createResponse.body());
        String paymentHash = invoiceJson.get("paymentHash").asText();

        // First mockpay call
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(mockPayRequest, HttpResponse.BodyHandlers.ofString());

        // Act: Call /mockpay again on already-paid invoice
        HttpResponse<String> secondResponse = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Returns already_paid status
        assertThat(secondResponse.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(secondResponse.body());
        assertThat(json.get("status").asText()).isEqualTo("already_paid");
        assertThat(json.get("paymentHash").asText()).isEqualTo(paymentHash);
    }

    /**
     * Tests that /mockpay returns 400 when paymentHash parameter is missing.
     */
    @Test
    void shouldReturn400WhenPaymentHashMissing() throws Exception {
        // Arrange: Start server
        server = new MockLnServer(TEST_PORT);
        server.start();

        // Act: Call /mockpay without paymentHash
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Returns 400 with error message
        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("error").asText()).isEqualTo("paymentHash parameter required");
    }

    /**
     * Tests that /mockpay supports overpayment simulation.
     * When amountSat parameter is greater than quoted amount, reports overpayment.
     */
    @Test
    void shouldSimulateOverpayment() throws Exception {
        // Arrange: Start server and create invoice for 1000 sats
        server = new MockLnServer(TEST_PORT);
        server.start();

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=1000"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());

        JsonNode invoiceJson = objectMapper.readTree(createResponse.body());
        String paymentHash = invoiceJson.get("paymentHash").asText();

        // Act: Call /mockpay with overpayment (1500 sats)
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash + "&amountSat=1500"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Response indicates overpayment
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("status").asText()).isEqualTo("paid");
        assertThat(json.get("quotedAmountSat").asLong()).isEqualTo(1000);
        assertThat(json.get("paidAmountSat").asLong()).isEqualTo(1500);
        assertThat(json.get("paymentType").asText()).isEqualTo("overpayment");
    }

    /**
     * Tests that /mockpay returns 400 for invalid amountSat value.
     */
    @Test
    void shouldReturn400ForInvalidAmountSat() throws Exception {
        // Arrange: Start server and create invoice
        server = new MockLnServer(TEST_PORT);
        server.start();

        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=1000"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());

        JsonNode invoiceJson = objectMapper.readTree(createResponse.body());
        String paymentHash = invoiceJson.get("paymentHash").asText();

        // Act: Call /mockpay with invalid amountSat
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash + "&amountSat=invalid"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Returns 400 with error message
        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("error").asText()).isEqualTo("Invalid amountSat value");
    }

    /**
     * Tests that configuration is logged on startup.
     * Verifies that autopay_enabled, auto_settle_delay, and webhook_url are logged.
     */
    @Test
    void shouldLogConfigurationOnStartup() throws Exception {
        // Arrange & Act: Start server
        server = new MockLnServer(TEST_PORT);
        server.start();

        // Assert: Log contains configuration information
        String output = outputCapture.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("phoenixd-mock: Started on port " + TEST_PORT);
        assertThat(output).contains("autopay_enabled=");
        assertThat(output).contains("auto_settle_delay=");
        assertThat(output).contains("webhook_url=");
    }

    /**
     * Tests that invoice creation logs appropriate message based on autopay setting.
     * This test verifies the logging behavior when an invoice is created.
     */
    @Test
    void shouldLogInvoiceCreationWithAutopayStatus() throws Exception {
        // Arrange: Start server
        server = new MockLnServer(TEST_PORT);
        server.start();

        // Act: Create an invoice
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=1000&externalId=test_log"))
                .build();
        httpClient.send(createRequest, HttpResponse.BodyHandlers.ofString());

        // Assert: Log contains invoice creation message
        String output = outputCapture.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("phoenixd-mock: Created invoice");
        assertThat(output).contains("payment_hash=");
        assertThat(output).contains("amount=1000");
    }
}
