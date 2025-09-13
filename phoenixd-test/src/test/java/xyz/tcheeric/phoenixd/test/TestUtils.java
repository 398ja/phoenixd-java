package xyz.tcheeric.phoenixd.test;

import xyz.tcheeric.phoenixd.operation.AbstractOperation;
import xyz.tcheeric.phoenixd.common.rest.util.Configuration;

import java.lang.reflect.Field;

public final class TestUtils {
    private static final String CONFIG_FIELD_NAME = "CONFIG";
    private static final String PHOENIXD_BASE_URL_KEY = "phoenixd.base_url";

    private TestUtils() {
    }

    public static void setBaseUrl(String baseUrl) {
        try {
            Field configField = AbstractOperation.class.getDeclaredField(CONFIG_FIELD_NAME);
            configField.setAccessible(true);
            Object cfg = configField.get(null);
            if (cfg instanceof Configuration configuration) {
                configuration.getProperties().setProperty(PHOENIXD_BASE_URL_KEY, baseUrl);
            } else if (cfg instanceof java.util.Properties props) {
                props.setProperty(PHOENIXD_BASE_URL_KEY, baseUrl);
            } else {
                throw new IllegalStateException("Unsupported CONFIG type: " + (cfg == null ? "null" : cfg.getClass()));
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
