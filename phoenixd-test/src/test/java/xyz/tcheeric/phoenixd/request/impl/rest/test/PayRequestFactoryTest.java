package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import xyz.tcheeric.phoenixd.request.impl.rest.PayRequestFactory;
import xyz.tcheeric.phoenixd.request.impl.rest.PayBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.PayLightningAddressRequest;

public class PayRequestFactoryTest {

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
