package xyz.tcheeric.phoenixd.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.common.rest.Response;

import java.util.List;

@Data
@NoArgsConstructor
public class DecodeInvoiceResponse implements Response {

    private Integer amount;
    private String description;

    @JsonIgnore
    private String chain;

    @JsonIgnore
    private String paymentHash;

    @JsonIgnore
    private Integer minFinalCltvExpiryDelta;

    @JsonIgnore
    private String paymentSecret;

    @JsonIgnore
    private String paymentMetadata;

    @JsonIgnore
    private List<List<ExtraHop>> extraHops;

    @JsonIgnore
    private Features features;

    @JsonIgnore
    private Long timestampSeconds;

    @Data
    @NoArgsConstructor
    public static class ExtraHop {
        private String nodeId;
        private String shortChannelId;
        private Integer feeBase;
        private Integer feeProportionalMillionths;
        private Integer cltvExpiryDelta;
    }

    @Data
    @NoArgsConstructor
    public static class Features {
        private Activated activated;
        private List<String> unknown;
    }

    @Data
    @NoArgsConstructor
    public static class Activated {
        private String var_onion_optin;
        private String payment_secret;
        private String basic_mpp;
        private String option_payment_metadata;
        private String trampoline_payment_experimental;
    }
}