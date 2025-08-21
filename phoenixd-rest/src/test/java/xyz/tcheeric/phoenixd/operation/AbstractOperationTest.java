package xyz.tcheeric.phoenixd.operation;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;

import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void executeThrowsOnNon2xxResponse() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(400));
        server.start();
        try {
            HttpRequest request = HttpRequest.newBuilder(server.url("/fail").uri()).GET().build();
            GetOperation operation = new GetOperation(request);
            assertThatThrownBy(operation::execute).isInstanceOf(Exception.class);
        } finally {
            server.shutdown();
        }
    }

    @Test
    void addHeaderReplacesExisting() {
        PostOperation op = new PostOperation("/items", "data");
        String original = op.getHeader("Authorization");
        op.addHeader("Authorization", "Basic new");
        assertThat(op.getHeader("Authorization")).isEqualTo("Basic new");
        assertThat(original).isNotEqualTo(op.getHeader("Authorization"));
    }

    @Test
    void removeHeaderUnsupported() {
        PostOperation op = new PostOperation("/items", "data");
        assertThatThrownBy(() -> op.removeHeader("X")).isInstanceOf(UnsupportedOperationException.class);
    }
}
