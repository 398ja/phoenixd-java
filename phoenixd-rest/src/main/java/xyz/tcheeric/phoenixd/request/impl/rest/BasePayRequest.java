package xyz.tcheeric.phoenixd.request.impl.rest;

import xyz.tcheeric.phoenixd.common.Request;
import xyz.tcheeric.phoenixd.common.Response;
import xyz.tcheeric.phoenixd.request.impl.PostRequest;

public class BasePayRequest<T extends Request.Param, U extends Response> extends PostRequest<T, U> {

    public BasePayRequest(String path, T param) {
        super(path, param);
    }
}
