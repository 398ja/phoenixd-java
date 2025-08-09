package xyz.tcheeric.phoenixd.operation;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathVariableReplacementTest {

    static class MultiPathParam implements Request.Param {
        private final String id;
        private final String subId;

        MultiPathParam(String id, String subId) {
            this.id = id;
            this.subId = subId;
        }

        @Override
        public String toString() {
            return "";
        }
    }

    @Test
    public void replacesMultipleVariables() {
        MultiPathParam param = new MultiPathParam("123", "456");
        GetOperation operation = new GetOperation("/path/{id}/child/{subId}", param);
        assertEquals("http://localhost:9740/path/123/child/456", operation.getHttpRequest().uri().toString());
    }
}
