package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.request.impl.rest.BasePayRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayRequestFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import xyz.tcheeric.phoenixd.request.impl.rest.PayLightningAddressRequest;

public class PayRequestFactoryTest {

    // Ensures factory handles uppercase Bolt11 invoices
    @Test
    public void testCreatePayRequestWithUppercaseInvoice() {
        String invoice = "LNBC1TESTINVOICE";

        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest(invoice);

        assertNotNull(request);
        assertEquals(PayBolt11InvoiceRequest.class, request.getClass());
    }
    
    // Verifies lightning address inputs produce corresponding requests
    @Test
    public void testCreateLightningAddressRequest() {
        assertTrue(PayRequestFactory.createPayRequest("alice@example.com") instanceof PayLightningAddressRequest);
    }

    // Checks that lowercase invoices produce Bolt11 requests
    @Test
    public void testCreateBolt11InvoiceRequest() {
        assertTrue(PayRequestFactory.createPayRequest("lnbc1u1pw0kx7pp5") instanceof PayBolt11InvoiceRequest);
    }

    // Confirms invalid strings cause an exception
    @Test
    public void testInvalidRequestThrows() {
        assertThrows(IllegalArgumentException.class, () -> PayRequestFactory.createPayRequest("invalid"));
    }
}
