package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetLightningAddressResponseTest {

    @Test
    void settersGettersAndToString() {
        GetLightningAddressResponse response = new GetLightningAddressResponse();
        response.setLightningAddress("user@host");

        assertThat(response.getLightningAddress()).isEqualTo("user@host");
        assertThat(response.toString()).contains("lightningAddress=user@host");

        GetLightningAddressResponse response2 = new GetLightningAddressResponse(null);
        assertThat(response2.getLightningAddress()).isNull();
        assertThat(response2.toString()).contains("lightningAddress=null");
    }
}

