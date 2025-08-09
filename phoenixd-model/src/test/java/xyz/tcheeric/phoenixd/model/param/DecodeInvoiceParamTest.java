package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DecodeInvoiceParamTest {

    @Test
    void settersGettersAndToString() {
        DecodeInvoiceParam param = new DecodeInvoiceParam();
        param.setInvoice("invoice123");

        assertThat(param.getInvoice()).isEqualTo("invoice123");
        assertThat(param.toString()).isEqualTo("invoice=invoice123");

        param.setInvoice(null);
        assertThat(param.toString()).isEqualTo("invoice=null");
    }
}

