package xyz.tcheeric.phoenixd.request.impl.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayRequestFactoryTest {

    @Test
    void createsLightningAddressRequest() {
        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest("user@example.com");
        assertThat(request).isInstanceOf(PayLightningAddressRequest.class);
    }

    @Test
    void createsBolt11InvoiceRequest() {
        BasePayRequest<?, ?> request = PayRequestFactory.createPayRequest("lnbc1abcde");
        assertThat(request).isInstanceOf(PayBolt11InvoiceRequest.class);
    }

    @Test
    void throwsOnInvalidRequest() {
        assertThatThrownBy(() -> PayRequestFactory.createPayRequest("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullRequestThrows() {
        assertThatThrownBy(() -> PayRequestFactory.createPayRequest(null))
                .isInstanceOf(NullPointerException.class);
    }
}
