package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.request.impl.rest.BasePayRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayRequestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import xyz.tcheeric.phoenixd.request.impl.rest.PayLightningAddressRequest;

public class PayRequestFactoryTest {

    @Test
    public void testCreatePayRequestWithUppercaseInvoice() {
        String invoice = "LNBC1TESTINVOICE";

        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest(invoice);

        assertNotNull(request);
        assertEquals(PayBolt11InvoiceRequest.class, request.getClass());
    }
    
    @Test
    public void testCreateLightningAddressRequest() {
        assertTrue(PayRequestFactory.createPayRequest("alice@example.com") instanceof PayLightningAddressRequest);
    }

    @Test
    public void testCreateBolt11InvoiceRequest() {
        assertTrue(PayRequestFactory.createPayRequest("lnbc1u1pw0kx7pp5") instanceof PayBolt11InvoiceRequest);
    }

    @Test
    public void testInvalidRequestThrows() {
        assertThrows(IllegalArgumentException.class, () -> PayRequestFactory.createPayRequest("invalid"));
    }
}
