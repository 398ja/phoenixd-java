package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInvoiceParamTest {

    // Ensures toString properly URL-encodes provided values
    @Test
    void toStringEncodesValues() throws MalformedURLException {
        CreateInvoiceParam param = new CreateInvoiceParam();
        param.setDescription("hello world");
        param.setAmountSat(1);
        param.setExpirySeconds(2);
        param.setExternalId("id=1&other");
        param.setWebhookUrl(new URL("https://example.com/hook?foo=bar&baz=qux"));

        String result = param.toString();

        assertThat(result)
                .contains("description=hello+world")
                .contains("externalId=id%3D1%26other")
                .contains("webhookUrl=https%3A%2F%2Fexample.com%2Fhook%3Ffoo%3Dbar%26baz%3Dqux");
    }

    // Verifies getters, setters and toString when a webhook URL is supplied
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

    // Confirms toString omits the webhook URL when it is not set
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
