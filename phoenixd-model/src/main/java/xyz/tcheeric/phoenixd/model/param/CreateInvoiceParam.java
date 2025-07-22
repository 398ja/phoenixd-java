package xyz.tcheeric.phoenixd.model.param;

import lombok.Data;
import xyz.tcheeric.common.rest.Request;

import java.net.URL;

@Data
public class CreateInvoiceParam implements Request.Param {

    private String description;
    private Integer amountSat;
    private Integer expirySeconds;
    private String externalId;
    private URL webhookUrl;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("description=").append(description)
                .append("&amountSat=").append(amountSat)
                .append("&expirySeconds=").append(expirySeconds)
                .append("&externalId=").append(externalId);
        if (webhookUrl != null) {
            sb.append("&webhookUrl=").append(webhookUrl);
        }
        return sb.toString();
    }
}
