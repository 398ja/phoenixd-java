package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayInvoiceResponseTest {

    @Test
    void settersGettersAndToString() {
        PayInvoiceResponse response = new PayInvoiceResponse();
        response.setRecipientAmountSat(10);
        response.setRoutingFeeSat(1);
        response.setPaymentId("pid");
        response.setPaymentHash("hash");
        response.setPaymentPreimage(null);
        response.setReason("reason");

        assertThat(response.getRecipientAmountSat()).isEqualTo(10);
        assertThat(response.getRoutingFeeSat()).isEqualTo(1);
        assertThat(response.getPaymentId()).isEqualTo("pid");
        assertThat(response.getPaymentHash()).isEqualTo("hash");
        assertThat(response.getPaymentPreimage()).isNull();
        assertThat(response.getReason()).isEqualTo("reason");

        assertThat(response.toString()).contains(
                "recipientAmountSat=10",
                "routingFeeSat=1",
                "paymentId=pid",
                "paymentHash=hash",
                "paymentPreimage=null",
                "reason=reason");
    }
}

