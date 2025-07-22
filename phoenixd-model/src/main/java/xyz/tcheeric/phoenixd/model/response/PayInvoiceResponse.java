package xyz.tcheeric.phoenixd.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.common.rest.Response;

@Data
@NoArgsConstructor
public class PayInvoiceResponse implements Response {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer recipientAmountSat;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer routingFeeSat;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentId;

    private String paymentHash;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentPreimage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;
}
