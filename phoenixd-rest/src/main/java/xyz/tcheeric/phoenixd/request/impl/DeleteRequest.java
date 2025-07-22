package xyz.tcheeric.phoenixd.request.impl;

import lombok.NonNull;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.DeleteOperation;
import xyz.tcheeric.phoenixd.request.AbstractRequest;

public class DeleteRequest<T extends Request.Param, U extends Response> extends AbstractRequest<T, U> {

    public DeleteRequest(String path) {
        super(path, null, new DeleteOperation(path));
    }

    public DeleteRequest(String path, Param param) {
        super(path, param, new DeleteOperation(path, param));
    }
    
    public void removeHeader(@NonNull String key) {
        this.getOperation().removeHeader(key);
    }
}
