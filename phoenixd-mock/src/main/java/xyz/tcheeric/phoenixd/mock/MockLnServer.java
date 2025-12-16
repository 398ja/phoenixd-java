package xyz.tcheeric.phoenixd.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MockLnServer {
    /**
     * Bech32 character set used for encoding/decoding.
     */
    private static final String BECH32_CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    /**
     * Bech32 generator values used in checksum calculation.
     * These are fixed values defined by the Bech32 specification.
     */
    private static final int[] BECH32_GENERATOR = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    /**
     * Secure random instance for generating random invoice data.
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Tracks created invoices by payment hash for auto-settlement.
     */
    private final Map<String, InvoiceInfo> invoices = new ConcurrentHashMap<>();

    /**
     * Index of invoices by external ID (quote ID) for lookup.
     */
    private final Map<String, InvoiceInfo> invoicesByExternalId = new ConcurrentHashMap<>();

    /**
     * Scheduler for auto-settling invoices after a delay.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * HTTP client for sending webhook notifications.
     */
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();

    /**
     * Jackson ObjectMapper for JSON parsing.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Auto-settlement delay in seconds (configurable via environment variable).
     */
    private final int autoSettleDelaySeconds = Integer.parseInt(
            System.getenv().getOrDefault("PHOENIXD_AUTO_SETTLE_DELAY_SECONDS", "2")
    );

    /**
     * Whether to automatically settle invoices (default: true for dev/test).
     * Set to false to require external payment (production-like behavior).
     * Configurable via PHOENIXD_AUTOPAY_ENABLED environment variable.
     */
    private final boolean autopayEnabled = Boolean.parseBoolean(
            System.getenv().getOrDefault("PHOENIXD_AUTOPAY_ENABLED", "true")
    );

    /**
     * Webhook base URL (configurable via environment variable).
     */
    private final String webhookBaseUrl = System.getenv().getOrDefault(
            "PHOENIXD_WEBHOOK_BASE_URL",
            "http://cashu-gateway-rest:8080"
    );

    private HttpServer server;

    private final int port;

    /**
     * Holds information about a created invoice for tracking and settlement.
     */
    private static class InvoiceInfo {
        final String paymentHash;
        final String serialized;
        final long amountSat;
        final String externalId;
        boolean settled;

        InvoiceInfo(String paymentHash, String serialized, long amountSat, String externalId) {
            this.paymentHash = paymentHash;
            this.serialized = serialized;
            this.amountSat = amountSat;
            this.externalId = externalId;
            this.settled = false;
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/getlnaddress", this::handleGetLightningAddress);
        server.createContext("/paylnaddress", this::handlePayLightningAddress);
        server.createContext("/createinvoice", this::handleCreateInvoice);
        server.createContext("/getinvoice", this::handleGetInvoice);
        server.createContext("/decodeinvoice", this::handleDecodeInvoice);
        server.createContext("/payinvoice", this::handlePayInvoice);
        server.createContext("/delete", this::handleDelete);
        server.createContext("/patch", this::handlePatch);
        server.createContext("/mockpay", this::handleMockPay);
        server.setExecutor(null);
        server.start();

        System.out.println("phoenixd-mock: Started on port " + port +
                " autopay_enabled=" + autopayEnabled +
                " auto_settle_delay=" + autoSettleDelaySeconds + "s" +
                " webhook_url=" + webhookBaseUrl);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
        scheduler.shutdownNow();
    }

    private void handleGetLightningAddress(HttpExchange exchange) throws IOException {
        writeString(exchange, "\u20BF398ja@strike.me");
    }

    private void handlePayLightningAddress(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"recipientAmountSat\":10}");
    }

    private void handleCreateInvoice(HttpExchange exchange) throws IOException {
        // Parse request body (application/x-www-form-urlencoded) to extract externalId
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String externalId = null;
        long amountSat = 10;

        if (body != null && !body.isEmpty()) {
            for (String param : body.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2) {
                    if ("externalId".equals(pair[0])) {
                        String value = java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                        externalId = value.isEmpty() ? null : value;
                    } else if ("amountSat".equals(pair[0])) {
                        try {
                            amountSat = Long.parseLong(pair[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        // Generate a unique mock bolt11 invoice with valid Bech32 encoding
        String invoiceId = Long.toHexString(System.nanoTime());
        String paymentHash = "hash" + invoiceId;
        String bolt11 = generateValidBolt11Invoice();

        // Track the invoice for auto-settlement
        InvoiceInfo invoiceInfo = new InvoiceInfo(paymentHash, bolt11, amountSat, externalId);
        invoices.put(paymentHash, invoiceInfo);

        // Also index by external ID (quote ID) for mockpay lookup
        if (externalId != null && !externalId.isEmpty()) {
            invoicesByExternalId.put(externalId, invoiceInfo);
        }

        // Schedule auto-settlement only if enabled
        if (autopayEnabled) {
            scheduler.schedule(() -> autoSettleInvoice(paymentHash), autoSettleDelaySeconds, TimeUnit.SECONDS);
            System.out.println("phoenixd-mock: Created invoice payment_hash=" + paymentHash +
                    " external_id=" + externalId + " amount=" + amountSat +
                    " auto_settle_in=" + autoSettleDelaySeconds + "s");
        } else {
            System.out.println("phoenixd-mock: Created invoice payment_hash=" + paymentHash +
                    " external_id=" + externalId + " amount=" + amountSat +
                    " autopay=disabled (awaiting external payment or /mockpay)");
        }

        writeJson(exchange, "{\"amountSat\":" + amountSat + ",\"paymentHash\":\"" + paymentHash +
                "\",\"serialized\":\"" + bolt11 + "\"}");
    }

    private void handleGetInvoice(HttpExchange exchange) throws IOException {
        // Extract payment hash from query parameters (/getinvoice?paymentHash=hash...)
        String query = exchange.getRequestURI().getQuery();
        String paymentHash = null;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "paymentHash".equals(pair[0])) {
                    paymentHash = pair[1];
                    break;
                }
            }
        }

        InvoiceInfo invoice = invoices.get(paymentHash);
        if (invoice == null) {
            writeJson(exchange, "{\"error\":\"Invoice not found\"}");
            return;
        }

        // Return invoice status with isPaid field
        String status = invoice.settled ? "PAID" : "PENDING";
        writeJson(exchange, String.format(
                "{\"paymentHash\":\"%s\",\"amountSat\":%d,\"serialized\":\"%s\",\"isPaid\":%b,\"status\":\"%s\"}",
                invoice.paymentHash,
                invoice.amountSat,
                invoice.serialized,
                invoice.settled,
                status
        ));
    }

    /**
     * Generates a minimally valid BOLT11 invoice for testing purposes.
     * Format: ln + currency + amount + separator + data + checksum
     *
     * This generates a mock invoice that passes basic Bech32 validation but is not
     * cryptographically valid for actual Lightning Network payments.
     */
    private String generateValidBolt11Invoice() {
        // Human-readable part: ln + bc (bitcoin mainnet) + 10n (10 nanosats = ~0 sats for testing)
        String hrp = "lnbc10n";

        // Generate random payment hash (32 bytes = 52 chars in bech32, roughly)
        // For a minimal valid invoice, we need at least timestamp + payment hash
        // Bech32 charset: qpzry9x8gf2tvdw0s3jn54khce6mua7l
        StringBuilder data = new StringBuilder();

        // Generate 52 random bech32 characters (represents ~32 bytes of data)
        for (int i = 0; i < 52; i++) {
            data.append(BECH32_CHARSET.charAt(SECURE_RANDOM.nextInt(BECH32_CHARSET.length())));
        }

        // Calculate and append Bech32 checksum (6 characters)
        String checksum = calculateBech32Checksum(hrp, data.toString());

        return hrp + "1" + data + checksum;
    }

    /**
     * Calculates Bech32 checksum for the given HRP and data.
     * Simplified implementation for mock purposes.
     */
    private String calculateBech32Checksum(String hrp, String data) {
        // Expand HRP
        int[] values = new int[hrp.length() * 2 + 1 + data.length() + 6];
        int idx = 0;
        for (int i = 0; i < hrp.length(); i++) {
            values[idx++] = hrp.charAt(i) >> 5;
        }
        values[idx++] = 0;
        for (int i = 0; i < hrp.length(); i++) {
            values[idx++] = hrp.charAt(i) & 31;
        }
        for (int i = 0; i < data.length(); i++) {
            values[idx++] = BECH32_CHARSET.indexOf(data.charAt(i));
        }
        for (int i = 0; i < 6; i++) {
            values[idx++] = 0;
        }

        // Calculate checksum using Bech32 polymod
        int polymod = polymod(values) ^ 1;

        StringBuilder checksum = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            checksum.append(BECH32_CHARSET.charAt((polymod >> (5 * (5 - i))) & 31));
        }

        return checksum.toString();
    }

    /**
     * Bech32 polymod function for checksum calculation.
     */
    private int polymod(int[] values) {
        int chk = 1;
        for (int value : values) {
            int top = chk >> 25;
            chk = (chk & 0x1ffffff) << 5 ^ value;
            for (int i = 0; i < 5; i++) {
                if (((top >> i) & 1) != 0) {
                    chk ^= BECH32_GENERATOR[i];
                }
            }
        }
        return chk;
    }

    private void handleDecodeInvoice(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"amount\":1000,\"description\":\"1 Blockaccino\"}");
    }

    /**
     * Handles /payinvoice endpoint for paying BOLT11 invoices.
     * Simulates instant payment success for testing purposes.
     */
    private void handlePayInvoice(HttpExchange exchange) throws IOException {
        // Parse request body to extract invoice parameter
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String invoice = null;

        if (body != null && !body.isEmpty()) {
            for (String param : body.split("&")) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2 && "invoice".equals(pair[0])) {
                    invoice = java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        if (invoice == null || invoice.isEmpty()) {
            writeJson(exchange, "{\"error\":\"Invoice required\"}");
            return;
        }

        // Generate mock payment response
        String paymentId = "payment-" + Long.toHexString(System.nanoTime());
        String paymentHash = "hash-" + Long.toHexString(System.nanoTime());
        long amountSat = 10; // Default test amount

        System.out.println("phoenixd-mock: Paying invoice invoice=" + invoice.substring(0, Math.min(20, invoice.length())) +
                "... payment_id=" + paymentId);

        // Return successful payment response
        writeJson(exchange, String.format(
                "{\"recipientAmountSat\":%d,\"paymentId\":\"%s\",\"paymentHash\":\"%s\"}",
                amountSat, paymentId, paymentHash
        ));
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"status\":\"deleted\"}");
    }

    private void handlePatch(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"status\":\"patched\"}");
    }

    /**
     * Simulates an external Lightning payment for testing purposes.
     *
     * When autopay is disabled, invoices remain in PENDING state indefinitely.
     * Since MockLnServer is not a real Lightning node, there's no way to actually
     * pay the invoice. This endpoint allows clients/tests to simulate that payment
     * was received, triggering the same flow as a real payment would:
     *
     * 1. Invoice marked as settled
     * 2. Webhook sent to cashu-gateway
     * 3. Payment state updated to PAID
     *
     * Parameters:
     * - paymentHash (required): The payment hash from invoice creation
     * - amountSat (optional): Override amount to simulate overpayment
     *   - If omitted: pays exact quoted amount (normal case)
     *   - If provided and > quoted: simulates overpayment (mint keeps excess)
     *   - If provided and < quoted: simulates underpayment (for testing only,
     *     wouldn't happen in production as BOLT11 payments are atomic)
     *
     * This enables testing of:
     * - Client invoice handling
     * - Payment polling flows
     * - Webhook delivery
     * - Post-payment minting
     * - Overpayment scenarios (mint should still issue quoted amount only)
     *
     * @param exchange HTTP exchange containing paymentHash and optional amountSat
     */
    private void handleMockPay(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String paymentHash = null;
        String externalId = null;
        Long amountSatOverride = null;

        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    if ("paymentHash".equals(pair[0])) {
                        paymentHash = pair[1];
                    } else if ("externalId".equals(pair[0])) {
                        externalId = pair[1];
                    } else if ("amountSat".equals(pair[0])) {
                        try {
                            amountSatOverride = Long.parseLong(pair[1]);
                        } catch (NumberFormatException e) {
                            writeJsonWithStatus(exchange, 400, "{\"error\":\"Invalid amountSat value\"}");
                            return;
                        }
                    }
                }
            }
        }

        // Require either paymentHash or externalId
        if (paymentHash == null && externalId == null) {
            writeJsonWithStatus(exchange, 400, "{\"error\":\"paymentHash or externalId parameter required\"}");
            return;
        }

        // Lookup invoice by paymentHash first, then by externalId
        InvoiceInfo invoice = null;
        if (paymentHash != null) {
            invoice = invoices.get(paymentHash);
        }
        if (invoice == null && externalId != null) {
            invoice = invoicesByExternalId.get(externalId);
            if (invoice != null) {
                paymentHash = invoice.paymentHash; // Use the actual payment hash for response
            }
        }
        if (invoice == null) {
            String lookupKey = paymentHash != null ? paymentHash : externalId;
            writeJsonWithStatus(exchange, 404, "{\"error\":\"Invoice not found\",\"lookupKey\":\"" + lookupKey + "\"}");
            return;
        }

        if (invoice.settled) {
            writeJson(exchange, "{\"status\":\"already_paid\",\"paymentHash\":\"" + paymentHash + "\"}");
            return;
        }

        // Determine payment amount
        long paidAmount = (amountSatOverride != null) ? amountSatOverride : invoice.amountSat;
        long quotedAmount = invoice.amountSat;

        // Log payment details
        String paymentType = "exact";
        if (amountSatOverride != null) {
            if (paidAmount > quotedAmount) {
                paymentType = "overpayment";
            } else if (paidAmount < quotedAmount) {
                paymentType = "underpayment (test only - wouldn't happen in production)";
            }
        }

        // Simulate payment received - marks as paid and sends webhook
        // Note: The webhook updates state to PAID but doesn't change the amount
        // The gateway/mint will issue tokens for the QUOTED amount, not paid amount
        autoSettleInvoice(paymentHash);

        System.out.println("phoenixd-mock: Mock payment received payment_hash=" + paymentHash +
                " quoted=" + quotedAmount + " paid=" + paidAmount + " type=" + paymentType);

        // Response includes both quoted and paid amounts for verification
        writeJson(exchange, "{\"status\":\"paid\"" +
                ",\"paymentHash\":\"" + paymentHash + "\"" +
                ",\"quotedAmountSat\":" + quotedAmount +
                ",\"paidAmountSat\":" + paidAmount +
                ",\"paymentType\":\"" + paymentType.split(" ")[0] + "\"}");
    }

    /**
     * Auto-settles an invoice by marking it as paid and sending a webhook notification.
     * This simulates a Lightning Network payment being received for testing purposes.
     */
    private void autoSettleInvoice(String paymentHash) {
        InvoiceInfo invoice = invoices.get(paymentHash);
        if (invoice == null || invoice.settled) {
            return;
        }

        invoice.settled = true;
        System.out.println("phoenixd-mock: Auto-settling invoice payment_hash=" + paymentHash +
                " amount=" + invoice.amountSat + " sat");

        // Note: Payment record is already created by PhoenixdGateway.pay()
        // so we don't need to create it here. We just update it to PAID via webhook.

        // Send webhook notification to gateway
        sendWebhookNotification(invoice);
    }

    /**
     * Updates the gateway quote state to PAID when an invoice is settled.
     * For RECEIVE quotes (minting), there's no payment record - only the quote needs updating.
     * Uses Spring Data REST PATCH endpoint to update quote state.
     */
    private void sendWebhookNotification(InvoiceInfo invoice) {
        try {
            if (invoice.externalId == null) {
                System.err.println("phoenixd-mock: No externalId for invoice payment_hash=" + invoice.paymentHash);
                return;
            }

            // Look up the quote using the invoice ID (externalId)
            String quoteSearchUrl = webhookBaseUrl + "/quote/search/findByInvoiceId?invoiceId=" +
                    java.net.URLEncoder.encode(invoice.externalId, StandardCharsets.UTF_8);

            HttpResponse<String> quoteResponse = httpClient.send(
                    HttpRequest.newBuilder().uri(URI.create(quoteSearchUrl)).GET().build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            if (quoteResponse.statusCode() < 200 || quoteResponse.statusCode() >= 300) {
                System.err.println("phoenixd-mock: Quote not found invoice_id=" + invoice.externalId);
                return;
            }

            // Extract quote self link and quoteId using Jackson JSON parsing
            String quoteBody = quoteResponse.body();
            JsonNode quoteJson = objectMapper.readTree(quoteBody);

            JsonNode quoteIdNode = quoteJson.get("quoteId");
            String quoteId = quoteIdNode != null ? quoteIdNode.asText() : "unknown";

            JsonNode linksNode = quoteJson.get("_links");
            if (linksNode == null) {
                System.err.println("phoenixd-mock: _links field not found in quote response");
                return;
            }
            JsonNode selfNode = linksNode.get("self");
            if (selfNode == null) {
                System.err.println("phoenixd-mock: self link not found in quote response");
                return;
            }
            JsonNode hrefNode = selfNode.get("href");
            if (hrefNode == null) {
                System.err.println("phoenixd-mock: href field not found in self link");
                return;
            }
            String quoteUrl = hrefNode.asText();

            // PATCH the quote to update state to PAID
            String payload = "{\"state\":\"PAID\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(quoteUrl))
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            String finalQuoteId = quoteId;  // For lambda capture
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            System.out.println("phoenixd-mock: Quote updated to PAID quote_id=" + finalQuoteId +
                                    " invoice_id=" + invoice.externalId + " status=" + response.statusCode());
                        } else {
                            System.err.println("phoenixd-mock: Quote update failed quote_id=" + finalQuoteId +
                                    " invoice_id=" + invoice.externalId + " status=" + response.statusCode() +
                                    " body=" + response.body());
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("phoenixd-mock: Quote update exception quote_id=" + finalQuoteId +
                                " invoice_id=" + invoice.externalId + " error=" + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("phoenixd-mock: Failed to update quote invoice_id=" + invoice.externalId +
                    " error=" + e.getMessage());
        }
    }

    private void writeJson(HttpExchange exchange, String json) throws IOException {
        writeJsonWithStatus(exchange, 200, json);
    }

    private void writeJsonWithStatus(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void writeString(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
