package xyz.tcheeric.phoenixd.model.param;

import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.common.rest.Request;

@Data
@NoArgsConstructor
public class DecodeInvoiceParam implements Request.Param {

    private String invoice;

    @Override
    public String toString() {
        return "invoice=" + invoice;
    }
}
