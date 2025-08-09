package xyz.tcheeric.phoenixd.request.impl.rest;

import lombok.NonNull;
import xyz.tcheeric.phoenixd.model.param.PayBolt11InvoiceParam;
import xyz.tcheeric.phoenixd.model.param.PayLightningAddressParam;

import java.util.regex.Pattern;

public class PayRequestFactory {

    /**
     * Matches BOLT11 invoices regardless of case.
     */
    private static final Pattern BOLT11_PATTERN =
            Pattern.compile("^(lnbc|lntb|lnsb|lnbcrt)[0-9]*[a-z0-9]+$",
                    Pattern.CASE_INSENSITIVE);

    public static BasePayRequest<?, ?> createPayRequest(@NonNull String request) {

        if (request.contains("@")) {
            PayLightningAddressParam param = new PayLightningAddressParam();
            param.setAddress(request);
            return new PayLightningAddressRequest(param);
        } else if (BOLT11_PATTERN.matcher(request).matches()) {
            PayBolt11InvoiceParam param = new PayBolt11InvoiceParam();
            param.setInvoice(request);
            return new PayBolt11InvoiceRequest(param);
        } else {
            throw new IllegalArgumentException("Invalid request format");
        }
    }
}