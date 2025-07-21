package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.request.impl.rest.BasePayRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayRequestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PayRequestFactoryTest {

    @Test
    public void testCreatePayRequestWithUppercaseInvoice() {
        String invoice = "LNBC1TESTINVOICE";

        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest(invoice);

        assertNotNull(request);
        assertEquals(PayBolt11InvoiceRequest.class, request.getClass());
    }
}
