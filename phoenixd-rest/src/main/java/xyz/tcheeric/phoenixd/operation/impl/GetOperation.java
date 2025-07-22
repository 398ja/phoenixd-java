package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.http.HttpRequest;

public class GetOperation extends AbstractOperation {

    public GetOperation(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @SneakyThrows
    public GetOperation(@NonNull String path) {
        super(Constants.HTTP_GET_METHOD, path, null);
    }

    @SneakyThrows
    public GetOperation(@NonNull String path, @NonNull Request.Param requestParam) {
        super(Constants.HTTP_GET_METHOD, path, requestParam, null);
    }
}
