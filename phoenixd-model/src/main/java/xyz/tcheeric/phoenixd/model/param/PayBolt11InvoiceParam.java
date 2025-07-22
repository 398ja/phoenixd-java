package xyz.tcheeric.phoenixd.model.param;

import lombok.Data;
import xyz.tcheeric.common.rest.Request;

@Data
public class PayBolt11InvoiceParam implements Request.Param {

    private Integer amountSat = 0;
    private String invoice = "";

    @Override
    public String toString() {
        String sb = "amountSat=" + amountSat +
                    "&invoice=" + invoice;
        return sb;
    }
}