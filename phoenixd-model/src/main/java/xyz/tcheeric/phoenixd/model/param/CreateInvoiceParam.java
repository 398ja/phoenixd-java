package xyz.tcheeric.phoenixd.model.param;

import lombok.Data;
import xyz.tcheeric.phoenixd.common.rest.Request;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        sb.append("description=").append(encode(description))
                .append("&amountSat=").append(amountSat == null ? "" : amountSat)
                .append("&expirySeconds=").append(expirySeconds == null ? "" : expirySeconds)
                .append("&externalId=").append(encode(externalId));
        if (webhookUrl != null) {
            String urlString = webhookUrl.toString();
            if (urlString.contains("?")) {
                sb.append("&webhookUrl=").append(encode(urlString));
            } else {
                sb.append("&webhookUrl=").append(urlString);
            }
        }
        return sb.toString();
    }

    private static String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
