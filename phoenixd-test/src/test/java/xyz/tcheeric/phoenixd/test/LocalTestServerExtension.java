package xyz.tcheeric.phoenixd.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LocalTestServerExtension implements BeforeAllCallback, AfterAllCallback {
    private static final LocalTestServer SERVER = new LocalTestServer();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        SERVER.start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        SERVER.stop();
    }
}
