package xyz.tcheeric.phoenixd.operation;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.util.Configuration;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseUrlNormalizationTest {

    // Verifies base_url without scheme defaults to http
    @Test
    void baseUrlWithoutSchemeDefaultsToHttp() throws Exception {
        Field configField = AbstractOperation.class.getDeclaredField("CONFIG");
        configField.setAccessible(true);
        Configuration cfg = (Configuration) configField.get(null);
        cfg.getProperties().setProperty("phoenixd.base_url", "localhost:9999");
        cfg.getProperties().setProperty("phoenixd.username", "user");
        cfg.getProperties().setProperty("phoenixd.password", "pass");

        PostOperation op = new PostOperation("/items", "data");
        assertThat(op.getHttpRequest().uri().getScheme()).isEqualTo("http");
        assertThat(op.getHttpRequest().uri().toString()).isEqualTo("http://localhost:9999/items");
    }
}

