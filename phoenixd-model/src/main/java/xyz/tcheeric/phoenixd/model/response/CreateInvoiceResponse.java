package xyz.tcheeric.phoenixd.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.common.rest.Response;

@Data
@NoArgsConstructor
public class CreateInvoiceResponse implements Response {

    private Integer amountSat;
    private String paymentHash;
    private String serialized;
}
