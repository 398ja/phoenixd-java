package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.http.HttpRequest;

@Slf4j
public class PostOperation extends AbstractOperation {

    public PostOperation(HttpRequest httpRequest) {
        super(httpRequest);
    }

    public PostOperation(@NonNull String path, @NonNull String data) {
        super(Constants.HTTP_POST_METHOD, path, data);
        this.addHeader("Content-Type", "application/x-www-form-urlencoded");
        if (log.isDebugEnabled()) log.debug("Initialized POST operation for path={} with body size={}", path, data.length());
    }

    @SneakyThrows
    public PostOperation(@NonNull String path, @NonNull Request.Param param, @NonNull String data) {
        super(Constants.HTTP_POST_METHOD, path, param, data);
        this.addHeader("Content-Type", "application/x-www-form-urlencoded");
        if (log.isDebugEnabled()) log.debug("Initialized POST operation for path={} with params and body size={}", path, data.length());
    }
}
