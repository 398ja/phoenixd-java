package xyz.tcheeric.phoenixd.test;

import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import java.lang.reflect.Field;
import java.util.Properties;

public final class TestUtils {
    private static final String CONFIG_FIELD_NAME = "CONFIG";
    private static final String PHOENIXD_BASE_URL_KEY = "phoenixd.base_url";

    private TestUtils() {
    }

    public static void setBaseUrl(String baseUrl) {
        try {
            Field configField = AbstractOperation.class.getDeclaredField(CONFIG_FIELD_NAME);
            configField.setAccessible(true);
            Properties props = (Properties) configField.get(null);
            props.setProperty(PHOENIXD_BASE_URL_KEY, baseUrl);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
