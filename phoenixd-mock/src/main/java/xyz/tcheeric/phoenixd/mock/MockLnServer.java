package xyz.tcheeric.phoenixd.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@RequiredArgsConstructor
public class MockLnServer {
    /**
     * Bech32 generator values used in checksum calculation.
     * These are fixed values defined by the Bech32 specification.
     */
    private static final int[] BECH32_GENERATOR = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    private HttpServer server;

    private final int port;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/getlnaddress", this::handleGetLightningAddress);
        server.createContext("/paylnaddress", this::handlePayLightningAddress);
        server.createContext("/createinvoice", this::handleCreateInvoice);
        server.createContext("/decodeinvoice", this::handleDecodeInvoice);
        server.createContext("/delete", this::handleDelete);
        server.createContext("/patch", this::handlePatch);
        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private void handleGetLightningAddress(HttpExchange exchange) throws IOException {
        writeString(exchange, "\u20BF398ja@strike.me");
    }

    private void handlePayLightningAddress(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"recipientAmountSat\":10}");
    }

    private void handleCreateInvoice(HttpExchange exchange) throws IOException {
        // Generate a unique mock bolt11 invoice with valid Bech32 encoding
        String invoiceId = Long.toHexString(System.nanoTime());
        String bolt11 = generateValidBolt11Invoice();
        writeJson(exchange, "{\"amountSat\":10,\"paymentHash\":\"hash" + invoiceId + "\",\"serialized\":\"" + bolt11 + "\"}");
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
        SecureRandom random = new SecureRandom();
        StringBuilder data = new StringBuilder();
        String charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

        // Generate 52 random bech32 characters (represents ~32 bytes of data)
        for (int i = 0; i < 52; i++) {
            data.append(charset.charAt(random.nextInt(charset.length())));
        }

        // Calculate and append Bech32 checksum (6 characters)
        String checksum = calculateBech32Checksum(hrp, data.toString());

        return hrp + "1" + data.toString() + checksum;
    }

    /**
     * Calculates Bech32 checksum for the given HRP and data.
     * Simplified implementation for mock purposes.
     */
    private String calculateBech32Checksum(String hrp, String data) {
        String charset = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

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
            values[idx++] = charset.indexOf(data.charAt(i));
        }
        for (int i = 0; i < 6; i++) {
            values[idx++] = 0;
        }

        // Calculate checksum using Bech32 polymod
        int polymod = polymod(values) ^ 1;

        StringBuilder checksum = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            checksum.append(charset.charAt((polymod >> (5 * (5 - i))) & 31));
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

    private void handleDelete(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"status\":\"deleted\"}");
    }

    private void handlePatch(HttpExchange exchange) throws IOException {
        writeJson(exchange, "{\"status\":\"patched\"}");
    }

    private void writeJson(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        writeString(exchange, json);
    }

    private void writeString(HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
