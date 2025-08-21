package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayLightningAddressInvoiceResponseTest {

    // Checks values and string output for lightning address payment invoices
    @Test
    void settersGettersAndToString() {
        PayLightningAddressInvoiceResponse response = new PayLightningAddressInvoiceResponse();
        response.setRecipientAmountSat(30);
        response.setRoutingFeeSat(3);
        response.setPaymentId("pid");
        response.setPaymentHash("hash");
        response.setPaymentPreimage(null);
        response.setReason("reason");

        assertThat(response.getRecipientAmountSat()).isEqualTo(30);
        assertThat(response.getRoutingFeeSat()).isEqualTo(3);
        assertThat(response.getPaymentId()).isEqualTo("pid");
        assertThat(response.getPaymentHash()).isEqualTo("hash");
        assertThat(response.getPaymentPreimage()).isNull();
        assertThat(response.getReason()).isEqualTo("reason");

        assertThat(response.toString()).contains(
                "recipientAmountSat=30",
                "routingFeeSat=3",
                "paymentId=pid",
                "paymentHash=hash",
                "paymentPreimage=null",
                "reason=reason");
    }
}

