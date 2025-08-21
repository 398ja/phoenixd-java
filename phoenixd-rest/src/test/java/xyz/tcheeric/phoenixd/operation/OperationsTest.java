package xyz.tcheeric.phoenixd.operation;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.operation.impl.DeleteOperation;
import xyz.tcheeric.phoenixd.operation.impl.PatchOperation;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;
import xyz.tcheeric.phoenixd.operation.impl.GetOperation;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationsTest {

    static class PathParam implements Request.Param {
        String id = "123";
        @Override
        public String toString() {
            return "id=" + id;
        }
    }

    @Test
    void postOperationSetsContentTypeAndResolvesPath() {
        PostOperation op = new PostOperation("/items/{id}", new PathParam(), "data");
        assertThat(op.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(op.getHttpRequest().uri().getPath()).isEqualTo("/items/123");
    }

    @Test
    void postOperationWithBodyOnlySetsHeader() {
        PostOperation op = new PostOperation("/items", "data");
        assertThat(op.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void postOperationFromHttpRequest() {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost/post"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        PostOperation op = new PostOperation(request);
        assertThat(op.getHttpRequest().uri().getPath()).isEqualTo("/post");
    }

    @Test
    void patchOperationAddsHeader() {
        PatchOperation op = new PatchOperation("/patch");
        op.addHeader("X-Test", "value");
        assertThat(op.getHeader("X-Test")).isEqualTo("value");
    }

    @Test
    void patchOperationRemoveHeaderThrows() {
        PatchOperation op = new PatchOperation("/patch");
        assertThatThrownBy(() -> op.removeHeader("X"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void deleteOperationRemovesHeader() {
        DeleteOperation op = new DeleteOperation("/delete");
        assertThat(op.getHeader("Authorization")).isNotNull();
        op.removeHeader("Authorization");
        assertThat(op.getHeader("Authorization")).isNull();
    }

    @Test
    void deleteOperationWithParamResolvesPath() {
        DeleteOperation op = new DeleteOperation("/items/{id}", new PathParam());
        assertThat(op.getHttpRequest().uri().getPath()).isEqualTo("/items/123");
    }

    @Test
    void deleteOperationFromHttpRequest() {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost/delete"))
                .DELETE()
                .build();
        DeleteOperation op = new DeleteOperation(request);
        assertThat(op.getHttpRequest().uri().getPath()).isEqualTo("/delete");
    }

    @Test
    void getOperationWithQueryParamAppendsQuery() {
        class QueryParam extends PathParam {
            @Override
            public Request.Param.Kind getKind() {
                return Request.Param.Kind.QUERY;
            }
        }
        GetOperation op = new GetOperation("/query", new QueryParam());
        assertThat(op.getHttpRequest().uri().getQuery()).isEqualTo("id=123");
    }

    @Test
    void nullArgumentsThrow() {
        assertThatThrownBy(() -> new PostOperation(null, "data")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostOperation("/x", (Request.Param) null, "data")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostOperation("/x", new PathParam(), null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PatchOperation((String) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PatchOperation("/x", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeleteOperation((String) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeleteOperation("/x", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new GetOperation((String) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new GetOperation("/x", null)).isInstanceOf(NullPointerException.class);
    }
}
