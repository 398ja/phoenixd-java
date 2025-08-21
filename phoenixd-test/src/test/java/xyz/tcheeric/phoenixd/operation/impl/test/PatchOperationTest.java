package xyz.tcheeric.phoenixd.operation.impl.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.PatchOperation;
import xyz.tcheeric.phoenixd.request.impl.PatchRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LocalTestServerExtension.class)
public class PatchOperationTest {

    static class PatchResponse implements Response {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    static class TestPatchRequest extends PatchRequest<Request.Param, PatchResponse> {
        public TestPatchRequest() {
            super("/patch");
        }
    }

    // Ensures PATCH requests use proper method, headers, and response mapping
    @Test
    public void testPatchOperation() {
        TestPatchRequest request = new TestPatchRequest();

        assertEquals("/patch", request.getPath());
        assertEquals(PatchOperation.class, request.getOperation().getClass());
        assertEquals("PATCH", ((PatchOperation) request.getOperation()).getHttpRequest().method());

        assertNotNull(request.getOperation().getHeader("Authorization"));
        assertThrows(UnsupportedOperationException.class, () -> request.getOperation().addHeader("x", "y"));
        assertThrows(UnsupportedOperationException.class, () -> request.getOperation().removeHeader("Authorization"));

        PatchResponse response = request.getResponse();
        assertEquals("patched", response.getStatus());
    }
}
