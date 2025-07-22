package xyz.tcheeric.phoenixd.request.impl;

import lombok.Getter;
import lombok.NonNull;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.PatchOperation;
import xyz.tcheeric.phoenixd.request.AbstractRequest;

@Getter
public class PatchRequest<T extends Request.Param, U extends Response> extends AbstractRequest<T, U> {

    public PatchRequest(@NonNull String path) {
        super(path, null, new PatchOperation(path));
    }

    public PatchRequest(@NonNull String path, @NonNull Param param) {
        super(path, param, new PatchOperation(path, param));
    }
}
