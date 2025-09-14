package xyz.tcheeric.phoenixd.operation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseUrlMissingTest {

    @AfterEach
    void cleanup() {
        // Reset any system property override to avoid side effects on other tests
        System.clearProperty("phoenixd.base_url");
    }

    // Ensures an explicit blank base_url triggers a clear configuration error
    @Test
    void blankBaseUrlThrows() {
        System.setProperty("phoenixd.base_url", "   ");
        assertThatThrownBy(() -> new PostOperation("/items", "data"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("phoenixd.base_url is not set");
    }

    // Verifies path normalization when base_url has scheme but path lacks leading slash
    @Test
    void pathWithoutLeadingSlashIsNormalized() {
        System.setProperty("phoenixd.base_url", "http://localhost:9740");
        PostOperation op = new PostOperation("items", "data");
        // URI should be http://localhost:9740/items (leading slash added)
        org.assertj.core.api.Assertions.assertThat(op.getHttpRequest().uri().toString())
                .isEqualTo("http://localhost:9740/items");
    }
}

