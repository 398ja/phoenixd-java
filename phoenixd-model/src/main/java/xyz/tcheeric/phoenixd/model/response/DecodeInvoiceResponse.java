package xyz.tcheeric.phoenixd.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.phoenixd.common.rest.Response;

import java.util.List;

/**
 * phoenixd's answer to {@code /decodeinvoice}.
 *
 * <p>{@code paymentHash} is read from the response: callers such as payment-adapter look the
 * payment up by it. Fields this class does not model are ignored, so a newer phoenixd that
 * adds fields does not break decoding.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecodeInvoiceResponse implements Response {

    private Integer amount;
    private String description;

    @JsonIgnore
    private String chain;

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