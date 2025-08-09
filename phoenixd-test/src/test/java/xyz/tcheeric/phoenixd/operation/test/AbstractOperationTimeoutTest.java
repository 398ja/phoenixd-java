package xyz.tcheeric.phoenixd.operation.test;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractOperationTimeoutTest {

    static class TestOperation extends AbstractOperation {
        TestOperation() {
            super("GET", "/timeout", null);
        }
    }

    @Test
    void usesDefaultTimeoutWhenMissing() {
        TestOperation operation = new TestOperation();
        long actual = operation.getHttpRequest().timeout().orElseThrow().toMillis();
        assertEquals(AbstractOperation.DEFAULT_TIMEOUT, actual);
    }
}
