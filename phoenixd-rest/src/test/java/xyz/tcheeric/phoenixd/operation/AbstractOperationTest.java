package xyz.tcheeric.phoenixd.operation;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;

import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractOperationTest {

    @Test
    void executeMakesSingleNetworkCall() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("ok"));
        server.start();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(server.url("/test").uri())
                    .GET()
                    .build();
            GetOperation operation = new GetOperation(request);

            operation.execute();

            assertThat(server.getRequestCount()).isEqualTo(1);
            assertThat(operation.getResponseBody()).isEqualTo("ok");
        } finally {
            server.shutdown();
        }
    }
}
