package xyz.tcheeric.phoenixd.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tcheeric.common.rest.Response;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetLightningAddressResponse implements Response {
    private String lightningAddress;
}