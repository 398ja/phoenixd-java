package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInvoiceParamTest {

    @Test
    void settersGettersAndToStringWithWebhook() throws Exception {
        CreateInvoiceParam param = new CreateInvoiceParam();
        param.setDescription("desc");
        param.setAmountSat(1);
        param.setExpirySeconds(2);
        param.setExternalId("id");
        URL url = new URL("https://example.com");
        param.setWebhookUrl(url);

        assertThat(param.getDescription()).isEqualTo("desc");
        assertThat(param.getAmountSat()).isEqualTo(1);
        assertThat(param.getExpirySeconds()).isEqualTo(2);
        assertThat(param.getExternalId()).isEqualTo("id");
        assertThat(param.getWebhookUrl()).isEqualTo(url);

        assertThat(param.toString()).isEqualTo(
                "description=desc&amountSat=1&expirySeconds=2&externalId=id&webhookUrl=https://example.com");
    }

    @Test
    void toStringWithoutWebhook() {
        CreateInvoiceParam param = new CreateInvoiceParam();
        param.setDescription("desc");
        param.setAmountSat(1);
        param.setExpirySeconds(2);
        param.setExternalId("id");

        assertThat(param.toString())
                .isEqualTo("description=desc&amountSat=1&expirySeconds=2&externalId=id");
    }
}

