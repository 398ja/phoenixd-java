package xyz.tcheeric.phoenixd.request.impl.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayRequestFactoryTest {

    // Ensures lightning addresses produce the proper request type
    @Test
    void createsLightningAddressRequest() {
        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest("user@example.com");
        assertThat(request).isInstanceOf(PayLightningAddressRequest.class);
    }

    // Checks that Bolt11 invoices are routed to the correct request
    @Test
    void createsBolt11InvoiceRequest() {
        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest("lnbc1abcde");
        assertThat(request).isInstanceOf(PayBolt11InvoiceRequest.class);
    }

    // Verifies invalid strings result in an IllegalArgumentException
    @Test
    void throwsOnInvalidRequest() {
        assertThatThrownBy(() -> PayRequestFactory.createPayRequest("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // Confirms null inputs are rejected with a NullPointerException
    @Test
    void nullRequestThrows() {
        assertThatThrownBy(() -> PayRequestFactory.createPayRequest(null))
                .isInstanceOf(NullPointerException.class);
    }
}
