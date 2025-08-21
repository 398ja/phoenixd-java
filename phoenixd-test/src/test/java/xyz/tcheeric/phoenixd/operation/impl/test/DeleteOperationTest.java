package xyz.tcheeric.phoenixd.operation.impl.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.Response;
import xyz.tcheeric.phoenixd.operation.impl.DeleteOperation;
import xyz.tcheeric.phoenixd.request.impl.DeleteRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LocalTestServerExtension.class)
public class DeleteOperationTest {

    static class DeleteResponse implements Response {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    static class TestDeleteRequest extends DeleteRequest<Request.Param, DeleteResponse> {
        public TestDeleteRequest() {
            super("/delete");
        }
    }

    // Verifies DELETE operations handle headers and responses appropriately
    @Test
    public void testDeleteOperation() {
        TestDeleteRequest request = new TestDeleteRequest();

        assertEquals("/delete", request.getPath());
        assertEquals(DeleteOperation.class, request.getOperation().getClass());
        assertEquals("DELETE", ((DeleteOperation) request.getOperation()).getHttpRequest().method());

        assertNotNull(request.getOperation().getHeader("Authorization"));
        request.removeHeader("Authorization");
        assertNull(request.getOperation().getHeader("Authorization"));
        assertThrows(UnsupportedOperationException.class, () -> request.getOperation().addHeader("x", "y"));

        DeleteResponse response = request.getResponse();
        assertEquals("deleted", response.getStatus());
    }
}
