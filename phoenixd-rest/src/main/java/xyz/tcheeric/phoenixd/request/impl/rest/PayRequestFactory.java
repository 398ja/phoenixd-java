package xyz.tcheeric.phoenixd.request.impl.rest;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.model.param.PayBolt11InvoiceParam;
import xyz.tcheeric.phoenixd.model.param.PayLightningAddressParam;

import java.util.regex.Pattern;

@Slf4j
public class PayRequestFactory {

    /**
     * Matches BOLT11 invoices regardless of case.
     */
    private static final Pattern BOLT11_PATTERN =
            Pattern.compile("^(lnbc|lntb|lnsb|lnbcrt)[0-9]*[a-z0-9]+$",
                    Pattern.CASE_INSENSITIVE);

    public static BasePayRequest<?, ?> createPayRequest(@NonNull String request) {
        if (log.isDebugEnabled()) log.debug("Creating pay request for input of length {}", request.length());
        if (request.contains("@")) {
            PayLightningAddressParam param = new PayLightningAddressParam();
            param.setAddress(request);
            if (log.isDebugEnabled()) log.debug("Detected Lightning Address format");
            return new PayLightningAddressRequest(param);
        } else if (BOLT11_PATTERN.matcher(request).matches()) {
            PayBolt11InvoiceParam param = new PayBolt11InvoiceParam();
            param.setInvoice(request);
            if (log.isDebugEnabled()) log.debug("Detected BOLT11 invoice format");
            return new PayBolt11InvoiceRequest(param);
        } else {
            log.warn("Invalid pay request format; input begins with: {}", request.length() > 8 ? request.substring(0, 8) + "..." : request);
            throw new IllegalArgumentException("Invalid request format");
        }
    }
}
