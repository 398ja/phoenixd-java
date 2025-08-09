package xyz.tcheeric.phoenixd.model.param;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class CreateInvoiceParamTest {

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
}
