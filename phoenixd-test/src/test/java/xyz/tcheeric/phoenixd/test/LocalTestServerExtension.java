package xyz.tcheeric.phoenixd.test;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static xyz.tcheeric.phoenixd.test.TestUtils.setBaseUrl;

public class LocalTestServerExtension implements BeforeAllCallback, AfterAllCallback {
    private static final LocalTestServer SERVER = new LocalTestServer();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        SERVER.start();
        setBaseUrl("http://localhost:9740");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        SERVER.stop();
    }
}
