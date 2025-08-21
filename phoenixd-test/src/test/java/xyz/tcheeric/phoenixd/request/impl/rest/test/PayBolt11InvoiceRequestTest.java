package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.model.param.PayBolt11InvoiceParam;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;
import xyz.tcheeric.phoenixd.request.impl.rest.PayBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(LocalTestServerExtension.class)
public class PayBolt11InvoiceRequestTest {

    // Verifies the request initializes with the proper path, param, and operation
    @Test
    public void testConstructor() {
        // Arrange
        PayBolt11InvoiceParam payBolt11InvoiceParam = new PayBolt11InvoiceParam();
        payBolt11InvoiceParam.setInvoice("lnbc1u1pw0kx7pp5");
        payBolt11InvoiceParam.setAmountSat(10);
        PayBolt11InvoiceRequest payBolt11InvoiceRequest = new PayBolt11InvoiceRequest(payBolt11InvoiceParam);

        // Assert
        assertEquals("/payinvoice", payBolt11InvoiceRequest.getPath());
        assertEquals(payBolt11InvoiceParam, payBolt11InvoiceRequest.getParam());
        assertEquals(PostOperation.class, payBolt11InvoiceRequest.getOperation().getClass());
        assertEquals(payBolt11InvoiceParam.toString(), payBolt11InvoiceRequest.getOperation().getRequestData());
    }
}
