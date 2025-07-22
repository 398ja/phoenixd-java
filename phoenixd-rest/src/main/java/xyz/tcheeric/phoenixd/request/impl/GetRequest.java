package xyz.tcheeric.phoenixd.request.impl;

import lombok.Getter;
import lombok.NonNull;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;
import xyz.tcheeric.phoenixd.request.AbstractRequest;

@Getter
public class GetRequest<T extends Request.Param, U extends Response> extends AbstractRequest<T, U> {

    public GetRequest(@NonNull String path) {
        super(path, null, new GetOperation(path));
    }

    public GetRequest(@NonNull String path, @NonNull Param param) {
        super(path, param, new GetOperation(path, param));
    }

}
