package xyz.tcheeric.phoenixd.operation.impl;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.Operation;

import static org.assertj.core.api.Assertions.assertThat;

public class AddHeaderOperationTest {

    // Ensures adding a header preserves the HTTP method for GET operations
    @Test
    void addHeaderDoesNotChangeMethodForGetOperation() {
        GetOperation operation = new GetOperation("/test");
        Operation result = operation.addHeader("X-Test", "value");

        assertThat(result).isInstanceOf(GetOperation.class);
        assertThat(operation.getHttpRequest().method()).isEqualTo("GET");
        assertThat(operation.getHeader("X-Test")).isEqualTo("value");
    }

    // Verifies PATCH operations keep their method after adding headers
    @Test
    void addHeaderDoesNotChangeMethodForPatchOperation() {
        PatchOperation operation = new PatchOperation("/test");
        Operation result = operation.addHeader("X-Test", "value");

        assertThat(result).isInstanceOf(PatchOperation.class);
        assertThat(operation.getHttpRequest().method()).isEqualTo("PATCH");
        assertThat(operation.getHeader("X-Test")).isEqualTo("value");
    }
}
