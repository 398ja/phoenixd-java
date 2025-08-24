package xyz.tcheeric.phoenixd.mock;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        int port = getPort();

        // Validate the port
        if (port < 1024 || port > 65535) {
            log.error("Invalid port: {}. Expected range: 1024..65535", port);
            System.exit(1);
        }

        // Start the mock server
        LocalTestServer server = new LocalTestServer(port);

        // Stop server on Ctrl+C (SIGINT) or normal JVM shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutdown signal received. Stopping server on port {}...", port);
                server.stop();
                log.info("Server stopped.");
            } catch (Throwable t) {
                log.error("Error while stopping server", t);
            }
        }, "shutdown-hook"));

        server.start();
    }

    private static int getPort() {
        String port = System.getenv("phoenixd_mock_port");
        if (port == null || port.isEmpty()) {
            return 9740;
        }
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return 9740;
        }
    }
}