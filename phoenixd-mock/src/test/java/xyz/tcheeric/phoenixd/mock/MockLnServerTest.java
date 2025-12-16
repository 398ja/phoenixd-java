package xyz.tcheeric.phoenixd.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MockLnServer HTTP endpoints.
 * Covers all remaining endpoints not tested by MockLnServerAutopayTest.
 */
class MockLnServerTest {

    private static final int TEST_PORT = 19741;
    private static final String BASE_URL = "http://localhost:" + TEST_PORT;

    private MockLnServer server;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        objectMapper = new ObjectMapper();
        server = new MockLnServer(TEST_PORT);
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void shouldReturnLightningAddress() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getlnaddress"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("@strike.me");
    }

    @Test
    void shouldPayLightningAddress() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/paylnaddress"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("address=test@strike.me&amountSat=10"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("recipientAmountSat").asInt()).isEqualTo(10);
    }

    @Test
    void shouldCreateInvoiceWithDefaultAmount() throws Exception {
        // Act: Create invoice without specifying amountSat
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amountSat").asLong()).isEqualTo(10); // default amount
        assertThat(json.has("paymentHash")).isTrue();
        assertThat(json.has("serialized")).isTrue();
        // Verify bolt11 format
        String bolt11 = json.get("serialized").asText();
        assertThat(bolt11).startsWith("lnbc");
    }

    @Test
    void shouldGetInvoiceStatus() throws Exception {
        // Arrange: Create an invoice first
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=500"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String paymentHash = createJson.get("paymentHash").asText();

        // Act: Get invoice status
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getinvoice?paymentHash=" + paymentHash))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("paymentHash").asText()).isEqualTo(paymentHash);
        assertThat(json.get("amountSat").asLong()).isEqualTo(500);
        assertThat(json.has("serialized")).isTrue();
        assertThat(json.has("isPaid")).isTrue();
        assertThat(json.has("status")).isTrue();
    }

    @Test
    void shouldReturnErrorForUnknownInvoice() throws Exception {
        // Act: Get invoice status for non-existent payment hash
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getinvoice?paymentHash=nonexistent_hash"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("error").asText()).isEqualTo("Invoice not found");
    }

    @Test
    void shouldDecodeInvoice() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/decodeinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("invoice=lnbc10n..."))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amount").asInt()).isEqualTo(1000);
        assertThat(json.get("description").asText()).isEqualTo("1 Blockaccino");
    }

    @Test
    void shouldPayInvoice() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("invoice=lnbc10n1ptest..."))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("recipientAmountSat").asLong()).isEqualTo(10);
        assertThat(json.has("paymentId")).isTrue();
        assertThat(json.has("paymentHash")).isTrue();
    }

    @Test
    void shouldReturnErrorWhenPayingWithoutInvoice() throws Exception {
        // Act: Pay invoice without providing invoice parameter
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("error").asText()).isEqualTo("Invoice required");
    }

    @Test
    void shouldHandleDelete() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/delete"))
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("status").asText()).isEqualTo("deleted");
    }

    @Test
    void shouldHandlePatch() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/patch"))
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("status").asText()).isEqualTo("patched");
    }

    @Test
    void shouldStopServerGracefully() throws Exception {
        // Arrange: Server is already started in setUp()

        // Act: Stop the server
        server.stop();
        server = null; // Prevent tearDown from stopping again

        // Assert: Server should not accept connections
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getlnaddress"))
                .GET()
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // If we get here, the server is still running (unexpected)
            assertThat(false).as("Expected connection to fail after server stop").isTrue();
        } catch (Exception e) {
            // Expected - connection should fail
            assertThat(e).isNotNull();
        }
    }

    @Test
    void shouldStopServerMultipleTimes() throws Exception {
        // Act: Stop the server multiple times (should be idempotent)
        server.stop();
        server.stop(); // Second call should not throw

        server = null; // Prevent tearDown from stopping again
    }

    @Test
    void shouldGetPaidInvoiceStatus() throws Exception {
        // Arrange: Create an invoice and mark it as paid via /mockpay
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=750"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String paymentHash = createJson.get("paymentHash").asText();

        // Mark as paid
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(mockPayRequest, HttpResponse.BodyHandlers.ofString());

        // Act: Get invoice status after payment
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getinvoice?paymentHash=" + paymentHash))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("isPaid").asBoolean()).isTrue();
        assertThat(json.get("status").asText()).isEqualTo("PAID");
    }

    @Test
    void shouldCreateInvoiceWithExternalId() throws Exception {
        // Act
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=100&externalId=order-12345"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amountSat").asLong()).isEqualTo(100);
        assertThat(json.has("paymentHash")).isTrue();
    }

    @Test
    void shouldCreateInvoiceWithEmptyExternalId() throws Exception {
        // Act: Create invoice with empty externalId (should be treated as null)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=200&externalId="))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amountSat").asLong()).isEqualTo(200);
    }

    @Test
    void shouldCreateInvoiceWithInvalidAmountSat() throws Exception {
        // Act: Create invoice with invalid amountSat (should use default)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=notanumber"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Should use default amount of 10
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amountSat").asLong()).isEqualTo(10);
    }

    @Test
    void shouldSimulateUnderpayment() throws Exception {
        // Arrange: Create invoice for 1000 sats
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=1000"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String paymentHash = createJson.get("paymentHash").asText();

        // Act: Call /mockpay with underpayment (500 sats)
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?paymentHash=" + paymentHash + "&amountSat=500"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Response indicates underpayment
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("status").asText()).isEqualTo("paid");
        assertThat(json.get("quotedAmountSat").asLong()).isEqualTo(1000);
        assertThat(json.get("paidAmountSat").asLong()).isEqualTo(500);
        assertThat(json.get("paymentType").asText()).isEqualTo("underpayment");
    }

    @Test
    void shouldGetInvoiceWithMultipleQueryParams() throws Exception {
        // Arrange: Create an invoice
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=300"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String paymentHash = createJson.get("paymentHash").asText();

        // Act: Get invoice with extra query params
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/getinvoice?foo=bar&paymentHash=" + paymentHash + "&baz=qux"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("paymentHash").asText()).isEqualTo(paymentHash);
    }

    @Test
    void shouldPayInvoiceWithLongInvoiceString() throws Exception {
        // Act: Pay with a very long invoice string (tests truncation in logging)
        String longInvoice = "lnbc10n1p" + "x".repeat(100);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("invoice=" + longInvoice))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("recipientAmountSat").asLong()).isEqualTo(10);
    }

    @Test
    void shouldMockPayWithMultipleQueryParams() throws Exception {
        // Arrange: Create invoice
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=400"))
                .build();
        HttpResponse<String> createResponse = httpClient.send(createRequest,
                HttpResponse.BodyHandlers.ofString());
        JsonNode createJson = objectMapper.readTree(createResponse.body());
        String paymentHash = createJson.get("paymentHash").asText();

        // Act: Call /mockpay with additional unrecognized query params
        HttpRequest mockPayRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/mockpay?extra=param&paymentHash=" + paymentHash + "&another=value"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(mockPayRequest,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("status").asText()).isEqualTo("paid");
    }

    @Test
    void shouldHandleCreateInvoiceWithUrlEncodedExternalId() throws Exception {
        // Act: Create invoice with URL-encoded externalId containing special chars
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=50&externalId=order%3D123%26test"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.get("amountSat").asLong()).isEqualTo(50);
    }

    @Test
    void shouldHandlePayInvoiceWithUrlEncodedInvoice() throws Exception {
        // Act: Pay invoice with URL-encoded invoice string
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/payinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("invoice=lnbc10n1p%2Btest"))
                .build();
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Assert
        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode json = objectMapper.readTree(response.body());
        assertThat(json.has("paymentId")).isTrue();
    }

    @Test
    void shouldCreateMultipleInvoicesWithUniqueHashes() throws Exception {
        // Act: Create multiple invoices
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=100"))
                .build();
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/createinvoice"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("amountSat=200"))
                .build();

        HttpResponse<String> response1 = httpClient.send(request1,
                HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = httpClient.send(request2,
                HttpResponse.BodyHandlers.ofString());

        // Assert: Each invoice has a unique payment hash
        JsonNode json1 = objectMapper.readTree(response1.body());
        JsonNode json2 = objectMapper.readTree(response2.body());
        assertThat(json1.get("paymentHash").asText())
                .isNotEqualTo(json2.get("paymentHash").asText());
        assertThat(json1.get("serialized").asText())
                .isNotEqualTo(json2.get("serialized").asText());
    }
}
