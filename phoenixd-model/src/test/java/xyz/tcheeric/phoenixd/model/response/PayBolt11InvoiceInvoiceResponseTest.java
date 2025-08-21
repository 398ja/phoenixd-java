package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayBolt11InvoiceInvoiceResponseTest {

    // Validates Bolt11 payment response fields and string output
    @Test
    void settersGettersAndToString() {
        PayBolt11InvoiceInvoiceResponse response = new PayBolt11InvoiceInvoiceResponse();
        response.setRecipientAmountSat(20);
        response.setRoutingFeeSat(2);
        response.setPaymentId("pid");
        response.setPaymentHash("hash");
        response.setPaymentPreimage("preimage");
        response.setReason(null);

        assertThat(response.getPaymentPreimage()).isEqualTo("preimage");
        assertThat(response.getReason()).isNull();

        assertThat(response.toString()).contains(
                "recipientAmountSat=20",
                "routingFeeSat=2",
                "paymentId=pid",
                "paymentHash=hash",
                "paymentPreimage=preimage",
                "reason=null");
    }
}

