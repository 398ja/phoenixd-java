package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.net.http.HttpRequest;

public class PostOperation extends AbstractOperation {

    public PostOperation(HttpRequest httpRequest) {
        super(httpRequest);
    }

    public PostOperation(@NonNull String path, @NonNull String data) {
        super(Constants.HTTP_POST_METHOD, path, data);
        this.addHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    @SneakyThrows
    public PostOperation(@NonNull String path, @NonNull Request.Param param, @NonNull String data) {
        super(Constants.HTTP_POST_METHOD, path, param, data);
        this.addHeader("Content-Type", "application/x-www-form-urlencoded");
    }
}
