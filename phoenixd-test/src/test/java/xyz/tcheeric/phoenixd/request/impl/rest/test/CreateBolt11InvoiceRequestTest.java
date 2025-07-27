package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;
import xyz.tcheeric.phoenixd.model.param.CreateInvoiceParam;
import xyz.tcheeric.phoenixd.model.response.CreateInvoiceResponse;
import xyz.tcheeric.phoenixd.request.impl.rest.CreateBolt11InvoiceRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
