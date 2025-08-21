package xyz.tcheeric.phoenixd.request;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.Operation;
import xyz.tcheeric.phoenixd.common.rest.VoidRequestParam;
import xyz.tcheeric.phoenixd.common.rest.VoidResponse;
import xyz.tcheeric.phoenixd.model.response.CreateInvoiceResponse;
import xyz.tcheeric.phoenixd.model.response.GetLightningAddressResponse;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractRequestTest {

    @Test
    void parsesJsonResponse() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"amountSat\":1,\"paymentHash\":\"h\",\"serialized\":\"s\"}"));
        server.start();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(server.url("/invoice").uri()).GET().build();
            GetOperation op = new GetOperation(httpRequest);
            AbstractRequest<VoidRequestParam, CreateInvoiceResponse> req = new AbstractRequest<>("/invoice", null, op) {};
            CreateInvoiceResponse resp = req.getResponse();
            assertThat(resp.getAmountSat()).isEqualTo(1);
            assertThat(resp.getPaymentHash()).isEqualTo("h");
            assertThat(server.getRequestCount()).isEqualTo(1);
        } finally {
            server.shutdown();
        }
    }

    @Test
    void handlesLightningAddressWithPrefix() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("₿user@domain"));
        server.start();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(server.url("/ln").uri()).GET().build();
            GetOperation op = new GetOperation(httpRequest);
            AbstractRequest<VoidRequestParam, GetLightningAddressResponse> req = new AbstractRequest<>("/ln", null, op) {};
            GetLightningAddressResponse resp = req.getResponse();
            assertThat(resp.getLightningAddress()).isEqualTo("user@domain");
            assertThat(server.getRequestCount()).isEqualTo(1);
        } finally {
            server.shutdown();
        }
    }

    @Test
    void handlesLightningAddressWithoutPrefix() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(200).setBody("user@domain"));
        server.start();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(server.url("/ln").uri()).GET().build();
            GetOperation op = new GetOperation(httpRequest);
            AbstractRequest<VoidRequestParam, GetLightningAddressResponse> req = new AbstractRequest<>("/ln", null, op) {};
            GetLightningAddressResponse resp = req.getResponse();
            assertThat(resp.getLightningAddress()).isEqualTo("user@domain");
            assertThat(server.getRequestCount()).isEqualTo(1);
        } finally {
            server.shutdown();
        }
    }

    @Test
    void voidResponseDoesNotExecuteOperation() {
        class DummyOperation extends AbstractOperation {
            boolean executed = false;
            DummyOperation() {
                super(HttpRequest.newBuilder(URI.create("http://localhost/test")).GET().build());
            }
            @Override
            public Operation execute() {
                executed = true;
                return this;
            }
        }
        DummyOperation op = new DummyOperation();
        AbstractRequest<VoidRequestParam, VoidResponse> req = new AbstractRequest<>("/test", null, op) {};
        VoidResponse resp = req.getResponse();
        assertThat(resp).isNotNull();
        assertThat(op.executed).isFalse();
    }
}
