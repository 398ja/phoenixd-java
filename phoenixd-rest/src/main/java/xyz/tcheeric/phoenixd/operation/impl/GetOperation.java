package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.http.HttpRequest;

@Slf4j
public class GetOperation extends AbstractOperation {

    public GetOperation(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @SneakyThrows
    public GetOperation(@NonNull String path) {
        super(Constants.HTTP_GET_METHOD, path, null);
        if (log.isDebugEnabled()) log.debug("Initialized GET operation for path={}", path);
    }

    @SneakyThrows
    public GetOperation(@NonNull String path, @NonNull Request.Param requestParam) {
        super(Constants.HTTP_GET_METHOD, path, requestParam, null);
        if (log.isDebugEnabled()) log.debug("Initialized GET operation for path={} with params", path);
    }
}
