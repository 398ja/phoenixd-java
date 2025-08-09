package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.model.param.CreateInvoiceParam;
import xyz.tcheeric.phoenixd.model.response.CreateInvoiceResponse;
import xyz.tcheeric.phoenixd.request.impl.rest.CreateBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;
import xyz.tcheeric.phoenixd.test.TestUtils;

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

    @Test
    public void testConstructor() {
        // Arrange and Act
        CreateBolt11InvoiceRequest createBolt11InvoiceRequest = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Assert
        assertEquals("/createinvoice", createBolt11InvoiceRequest.getPath());
    }

    @Test
    public void testAddHeader() {
        // Arrange
        CreateBolt11InvoiceRequest createBolt11InvoiceRequest = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Act
        createBolt11InvoiceRequest.addHeader("key", "value");

        // Assert
        assertEquals("value", createBolt11InvoiceRequest.getOperation().getHeader("key"));
    }

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

    @Test
    public void testUriAndHeaders() {
        // Arrange
        CreateBolt11InvoiceRequest request = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());

        // Assert
        assertEquals("http://localhost:9740/createinvoice", request.getOperation().getHttpRequest().uri().toString());
        assertEquals("Basic Og==", request.getOperation().getHeader("Authorization"));
        assertEquals("application/x-www-form-urlencoded", request.getOperation().getHeader("Content-Type"));
    }

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
            TestUtils.setBaseUrl("http://localhost:9750");
            CreateBolt11InvoiceRequest request = new CreateBolt11InvoiceRequest(new CreateInvoiceParam());
            assertThrows(IOException.class, request::getResponse);
        } finally {
            errorServer.stop(0);
            TestUtils.setBaseUrl("http://localhost:9740");
        }
    }
}
