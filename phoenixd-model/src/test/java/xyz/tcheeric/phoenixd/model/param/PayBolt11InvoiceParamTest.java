package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayBolt11InvoiceParamTest {

    // Validates getters, setters and string representation of the param
    @Test
    void settersGettersAndToString() {
        PayBolt11InvoiceParam param = new PayBolt11InvoiceParam();
        param.setAmountSat(50);
        param.setInvoice("inv");

        assertThat(param.getAmountSat()).isEqualTo(50);
        assertThat(param.getInvoice()).isEqualTo("inv");
        assertThat(param.toString()).isEqualTo("amountSat=50&invoice=inv");

        param.setInvoice(null);
        assertThat(param.toString()).isEqualTo("amountSat=50&invoice=null");
    }
}

