package xyz.tcheeric.phoenixd.operation.impl;

import lombok.NonNull;
import lombok.SneakyThrows;
import xyz.tcheeric.common.rest.Operation;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.util.Constants;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

public class PatchOperation extends AbstractOperation {

    @SneakyThrows
    public PatchOperation(@NonNull String path) {
        super(Constants.HTTP_PATCH_METHOD, path, null);
    }

    @SneakyThrows
    public PatchOperation(@NonNull String path, @NonNull Request.Param param) {
        super(Constants.HTTP_PATCH_METHOD, path, param, null);
    }

    @Override
    public Operation removeHeader(String key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Operation addHeader(String key, String value) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
