package xyz.tcheeric.phoenixd.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class MockLnServer {
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
        writeJson(exchange, "{\"amountSat\":10,\"paymentHash\":\"hash\",\"serialized\":\"invoice\"}");
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
