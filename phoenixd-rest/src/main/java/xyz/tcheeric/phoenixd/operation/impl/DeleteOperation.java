package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.common.rest.Operation;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;

@Slf4j
public class DeleteOperation extends AbstractOperation {

    public DeleteOperation(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @SneakyThrows
    public DeleteOperation(@NonNull String path) {
        super(Constants.HTTP_DELETE_METHOD, path, null);
        if (log.isDebugEnabled()) log.debug("Initialized DELETE operation for path={}", path);
    }

    @SneakyThrows
    public DeleteOperation(@NonNull String path, @NonNull Request.Param requestParam) {
        super(Constants.HTTP_DELETE_METHOD, path, requestParam, null);
        if (log.isDebugEnabled()) log.debug("Initialized DELETE operation for path={} with params", path);
    }

    @Override
    public Operation removeHeader(@NonNull String key) {
        HttpHeaders headers = httpRequest.headers();
        Builder requestBuilder = HttpRequest.newBuilder(httpRequest.uri())
                .method(httpRequest.method(), httpRequest.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));

        headers.map().forEach((headerKey, headerValues) -> {
            if (!headerKey.equalsIgnoreCase(key)) {
                headerValues.forEach(value -> requestBuilder.header(headerKey, value));
            }
        });

        this.httpRequest = requestBuilder.build();
        if (log.isDebugEnabled()) log.debug("Removed header '{}' from DELETE request {} {}", key, httpRequest.method(), httpRequest.uri());
        return this;
    }

    @Override
    public Operation addHeader(String key, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
