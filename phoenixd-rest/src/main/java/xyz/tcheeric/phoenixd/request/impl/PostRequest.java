package xyz.tcheeric.phoenixd.request.impl;

import lombok.Getter;
import lombok.NonNull;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;
import xyz.tcheeric.phoenixd.request.AbstractRequest;

@Getter
public class PostRequest<T extends Request.Param, U extends Response> extends AbstractRequest<T, U> {

    public PostRequest(@NonNull String path, @NonNull Request.Param param) {
        super(path, param, new PostOperation(path, param.toString()));
    }

    public PostRequest(@NonNull String path, @NonNull String body) {
        super(path, null, new PostOperation(path, body));
    }

    public PostRequest(@NonNull String path, @NonNull Param param, @NonNull String body) {
        super(path, param, new PostOperation(path, param, body));
    }

    public void addHeader(@NonNull String key, @NonNull String value) {
        this.getOperation().addHeader(key, value);
    }
    
}
