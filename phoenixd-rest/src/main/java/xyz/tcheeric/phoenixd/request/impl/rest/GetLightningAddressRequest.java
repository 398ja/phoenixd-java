package xyz.tcheeric.phoenixd.request.impl.rest;

import xyz.tcheeric.phoenixd.common.VoidRequestParam;
import xyz.tcheeric.phoenixd.model.response.GetLightningAddressResponse;
import xyz.tcheeric.phoenixd.request.impl.GetRequest;

public class GetLightningAddressRequest extends GetRequest<VoidRequestParam, GetLightningAddressResponse> {

    public GetLightningAddressRequest() {
        super("/getlnaddress");
    }
}
