package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.common.rest.Operation;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

@Slf4j
public class PatchOperation extends AbstractOperation {

    @SneakyThrows
    public PatchOperation(@NonNull String path) {
        super(Constants.HTTP_PATCH_METHOD, path, null);
        if (log.isDebugEnabled()) log.debug("Initialized PATCH operation for path={}", path);
    }

    @SneakyThrows
    public PatchOperation(@NonNull String path, @NonNull Request.Param param) {
        super(Constants.HTTP_PATCH_METHOD, path, param, null);
        if (log.isDebugEnabled()) log.debug("Initialized PATCH operation for path={} with params", path);
    }

    @Override
    public Operation removeHeader(String key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Operation addHeader(String key, String value) {
        if (log.isDebugEnabled()) log.debug("Adding header on PATCH operation: {}=<redacted? {}>", key, key.equalsIgnoreCase("Authorization") ? "yes" : "no");
        return super.addHeader(key, value);
    }

}
