package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayLightningAddressParamTest {

    // Ensures setters, getters, and toString handle a message field
    @Test
    void settersGettersAndToStringWithMessage() {
        PayLightningAddressParam param = new PayLightningAddressParam();
        param.setAmountSat(70);
        param.setAddress("addr");
        param.setMessage("hello");

        assertThat(param.getAmountSat()).isEqualTo(70);
        assertThat(param.getAddress()).isEqualTo("addr");
        assertThat(param.getMessage()).isEqualTo("hello");
        assertThat(param.toString()).isEqualTo("amountSat=70&address=addr&message=hello");
    }

    // Confirms toString omits the message when it is empty or null
    @Test
    void toStringWithoutMessage() {
        PayLightningAddressParam param = new PayLightningAddressParam();
        param.setAmountSat(70);
        param.setAddress("addr");

        assertThat(param.toString()).isEqualTo("amountSat=70&address=addr");

        param.setMessage("");
        assertThat(param.toString()).isEqualTo("amountSat=70&address=addr");
    }
}

