package xyz.tcheeric.phoenixd.request.impl;

import org.junit.jupiter.api.Test;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.VoidRequestParam;
import xyz.tcheeric.phoenixd.common.rest.VoidResponse;
import xyz.tcheeric.phoenixd.model.param.CreateInvoiceParam;
import xyz.tcheeric.phoenixd.model.param.DecodeInvoiceParam;
import xyz.tcheeric.phoenixd.request.impl.rest.CreateBolt11InvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.DecodeInvoiceRequest;
import xyz.tcheeric.phoenixd.request.impl.rest.GetLightningAddressRequest;
import xyz.tcheeric.phoenixd.operation.AbstractOperation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestTest {

    static class DummyParam implements Request.Param {
        String id = "123";
        @Override
        public String toString() {
            return "";
        }
    }

    // Verifies header management and path substitution for POST and DELETE requests
    @Test
    void postAndDeleteRequestHeaders() {
        PostRequest<VoidRequestParam, VoidResponse> post = new PostRequest<>("/post", "body");
        post.addHeader("X-Test", "value");
        assertThat(post.getOperation().getHeader("X-Test")).isEqualTo("value");

        DummyParam param = new DummyParam();
        PostRequest<DummyParam, VoidResponse> postWithParam = new PostRequest<>("/post", param);
        postWithParam.addHeader("A", "B");
        PostRequest<DummyParam, VoidResponse> postWithParamAndBody = new PostRequest<>("/post/{id}", param, "data");
        postWithParamAndBody.addHeader("A", "B");
        assertThat(((xyz.tcheeric.phoenixd.operation.AbstractOperation) postWithParam.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/post");
        assertThat(((xyz.tcheeric.phoenixd.operation.AbstractOperation) postWithParamAndBody.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/post/123");
        assertThat(postWithParam.getParam()).isEqualTo(param);
        assertThat(postWithParamAndBody.getParam()).isEqualTo(param);

        DeleteRequest<VoidRequestParam, VoidResponse> delete = new DeleteRequest<>("/delete");
        assertThat(delete.getOperation().getHeader("Authorization")).isNotNull();
        delete.removeHeader("Authorization");
        assertThat(delete.getOperation().getHeader("Authorization")).isNull();

        DeleteRequest<DummyParam, VoidResponse> deleteWithParam = new DeleteRequest<>("/delete/{id}", new DummyParam());
        assertThat(((xyz.tcheeric.phoenixd.operation.AbstractOperation) deleteWithParam.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/delete/123");
        assertThat(deleteWithParam.getParam()).isNotNull();
    }

    // Checks GET and PATCH requests handle parameters and paths
    @Test
    void getAndPatchRequestConstructors() {
        GetRequest<VoidRequestParam, VoidResponse> getNoParam = new GetRequest<>("/get");
        GetRequest<DummyParam, VoidResponse> get = new GetRequest<>("/get", new DummyParam());
        PatchRequest<VoidRequestParam, VoidResponse> patchNoParam = new PatchRequest<>("/patch");
        PatchRequest<DummyParam, VoidResponse> patch = new PatchRequest<>("/patch", new DummyParam());
        assertThat(getNoParam.getPath()).isEqualTo("/get");
        assertThat(((AbstractOperation) getNoParam.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/get");
        assertThat(get.getPath()).isEqualTo("/get");
        assertThat(((AbstractOperation) get.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/get");
        assertThat(get.getParam()).isNotNull();
        assertThat(patchNoParam.getPath()).isEqualTo("/patch");
        assertThat(((AbstractOperation) patchNoParam.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/patch");
        assertThat(patch.getPath()).isEqualTo("/patch");
        assertThat(((AbstractOperation) patch.getOperation()).getHttpRequest().uri().getPath()).isEqualTo("/patch");
        assertThat(patch.getParam()).isNotNull();
    }

    // Ensures specialized request subclasses can be constructed
    @Test
    void specializedRequestsConstructors() {
        CreateInvoiceParam createParam = new CreateInvoiceParam();
        createParam.setDescription("desc");
        new CreateBolt11InvoiceRequest(createParam);

        DecodeInvoiceParam decodeParam = new DecodeInvoiceParam();
        decodeParam.setInvoice("lnbc1abcde");
        new DecodeInvoiceRequest(decodeParam);

        new GetLightningAddressRequest();
    }

    // Confirms POST request constructors throw on null arguments
    @Test
    void postRequestNullArgumentsThrow() {
        DummyParam param = new DummyParam();
        assertThatThrownBy(() -> new PostRequest<DummyParam, VoidResponse>(null, param))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostRequest<DummyParam, VoidResponse>("/post", (String) null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostRequest<DummyParam, VoidResponse>(null, param, "data"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostRequest<DummyParam, VoidResponse>("/post", (DummyParam) null, "data"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PostRequest<DummyParam, VoidResponse>("/post", param, null))
                .isInstanceOf(NullPointerException.class);
    }

    // Ensures PostRequest.addHeader validates inputs
    @Test
    void postRequestAddHeaderNullArguments() {
        PostRequest<VoidRequestParam, VoidResponse> request = new PostRequest<>("/post", "body");
        assertThatThrownBy(() -> request.addHeader(null, "v")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> request.addHeader("k", null)).isInstanceOf(NullPointerException.class);
    }

    // Validates other request types enforce non-null parameters
    @Test
    void otherRequestsNullArgumentsThrow() {
        DummyParam param = new DummyParam();
        assertThatThrownBy(() -> new GetRequest<VoidRequestParam, VoidResponse>(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new GetRequest<DummyParam, VoidResponse>("/get", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PatchRequest<VoidRequestParam, VoidResponse>(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new PatchRequest<DummyParam, VoidResponse>("/patch", null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeleteRequest<VoidRequestParam, VoidResponse>(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new DeleteRequest<DummyParam, VoidResponse>("/del", null)).isInstanceOf(NullPointerException.class);
    }
}
