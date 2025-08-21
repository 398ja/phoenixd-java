package xyz.tcheeric.phoenixd.model.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DecodeInvoiceResponseTest {

    // Checks getters, setters, and string output for complex invoice responses
    @Test
    void settersGettersAndToString() {
        DecodeInvoiceResponse response = new DecodeInvoiceResponse();
        response.setAmount(1);
        response.setDescription("desc");
        response.setChain("chain");
        response.setPaymentHash("hash");
        response.setMinFinalCltvExpiryDelta(2);
        response.setPaymentSecret("secret");
        response.setPaymentMetadata(null);
        response.setTimestampSeconds(123L);

        DecodeInvoiceResponse.ExtraHop hop = new DecodeInvoiceResponse.ExtraHop();
        hop.setNodeId("node");
        hop.setShortChannelId("scid");
        hop.setFeeBase(3);
        hop.setFeeProportionalMillionths(4);
        hop.setCltvExpiryDelta(5);
        response.setExtraHops(List.of(List.of(hop)));

        DecodeInvoiceResponse.Activated activated = new DecodeInvoiceResponse.Activated();
        activated.setVar_onion_optin("v");
        activated.setPayment_secret("ps");
        activated.setBasic_mpp("bmpp");
        activated.setOption_payment_metadata("opm");
        activated.setTrampoline_payment_experimental("tpe");

        DecodeInvoiceResponse.Features features = new DecodeInvoiceResponse.Features();
        features.setActivated(activated);
        features.setUnknown(List.of("x"));
        response.setFeatures(features);

        assertThat(response.getAmount()).isEqualTo(1);
        assertThat(response.getDescription()).isEqualTo("desc");
        assertThat(response.getChain()).isEqualTo("chain");
        assertThat(response.getPaymentHash()).isEqualTo("hash");
        assertThat(response.getMinFinalCltvExpiryDelta()).isEqualTo(2);
        assertThat(response.getPaymentSecret()).isEqualTo("secret");
        assertThat(response.getPaymentMetadata()).isNull();
        assertThat(response.getExtraHops()).hasSize(1);
        assertThat(response.getFeatures().getActivated().getBasic_mpp()).isEqualTo("bmpp");
        assertThat(response.getFeatures().getUnknown()).contains("x");

        assertThat(hop.toString()).contains("nodeId=node", "feeBase=3");
        assertThat(activated.toString()).contains("var_onion_optin=v", "trampoline_payment_experimental=tpe");
        assertThat(features.toString()).contains("unknown=[x]");

        assertThat(response.toString()).contains("amount=1", "description=desc", "chain=chain", "paymentMetadata=null");
    }
}

