package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.model.param.CreateInvoiceParam;
import xyz.tcheeric.phoenixd.model.response.CreateInvoiceResponse;
import xyz.tcheeric.phoenixd.request.impl.rest.CreateBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;
import xyz.tcheeric.phoenixd.test.TestUtils;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LocalTestServerExtension.class)
public class CreateBolt11InvoiceRequestTest {

    private static final Logger log = Logger.getLogger(CreateBolt11InvoiceRequestTest.class.getName());
    private static final int ERROR_SERVER_PORT = 9751;

    // Checks constructor initializes request with correct path
    @Test
    public void testConstructor() {
        // Arrange and Act
        CreateBolt11InvoiceRequest createBolt11InvoiceRequest = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Assert
        assertEquals("/createinvoice", createBolt11InvoiceRequest.getPath());
    }

    // Ensures headers can be added to the underlying operation
    @Test
    public void testAddHeader() {
        // Arrange
        CreateBolt11InvoiceRequest createBolt11InvoiceRequest = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Act
        createBolt11InvoiceRequest.addHeader("key", "value");

        // Assert
        assertEquals("value", createBolt11InvoiceRequest.getOperation().getHeader("key"));
    }

    // Validates response parsing for a successful invoice creation
    @Test
    public void testGetResponse() throws Exception {
        // Arrange
        CreateInvoiceParam param = new CreateInvoiceParam();
        param.setAmountSat(10);
        param.setExpirySeconds(3600);
        param.setDescription("test description: testGetResponse");

        CreateBolt11InvoiceRequest createBolt11InvoiceRequest = new CreateBolt11InvoiceRequest(param);

        // Act
        CreateInvoiceResponse response = createBolt11InvoiceRequest.getResponse();

        // Assert
        assertEquals(10, response.getAmountSat());
        assertNotNull(response.getSerialized());
        assertNotNull(response.getPaymentHash());
        log.log(Level.ALL, "Invoice: {0}", response.getSerialized());
    }

    // Verifies request URI and default headers are set properly
    @Test
    public void testUriAndHeaders() {
        // Arrange
        CreateBolt11InvoiceRequest request = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Assert
        assertEquals("http://localhost:9740/createinvoice", ((AbstractOperation) request.getOperation()).getHttpRequest().uri().toString());
        assertEquals("Basic Og==", request.getOperation().getHeader("Authorization"));
        assertEquals("application/x-www-form-urlencoded", request.getOperation().getHeader("Content-Type"));
    }

    // Confirms IOExceptions are thrown when server returns an error
    @Test
    public void testErrorHandling() throws Exception {
        HttpServer errorServer = HttpServer.create(new InetSocketAddress(ERROR_SERVER_PORT), 0);
        errorServer.createContext("/createinvoice", exchange -> {
            byte[] bytes = "error".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        errorServer.start();
        try {
            TestUtils.setBaseUrl("http://localhost:" + ERROR_SERVER_PORT);
            CreateBolt11InvoiceRequest request = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());
            assertThrows(IOException.class, request::getResponse);
        } finally {
            errorServer.stop(0);
            TestUtils.setBaseUrl("http://localhost:9740");
        }
    }
}
