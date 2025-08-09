package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInvoiceResponseTest {

    @Test
    void settersGettersAndToString() {
        CreateInvoiceResponse response = new CreateInvoiceResponse();
        response.setAmountSat(123);
        response.setPaymentHash("hash");
        response.setSerialized("serialized");

        assertThat(response.getAmountSat()).isEqualTo(123);
        assertThat(response.getPaymentHash()).isEqualTo("hash");
        assertThat(response.getSerialized()).isEqualTo("serialized");
        assertThat(response.toString()).contains("amountSat=123", "paymentHash=hash", "serialized=serialized");

        response.setPaymentHash(null);
        assertThat(response.toString()).contains("paymentHash=null");
    }
}

