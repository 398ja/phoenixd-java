package xyz.tcheeric.phoenixd.common.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTest {

    static class SampleParam implements Request.Param {
        String id = "123";
        String slug = "abc";
    }

    @Test
    void replacePathVariablesSubstitutesFields() throws Exception {
        Operation op = new Operation() {
            @Override public Operation execute() { return this; }
            @Override public String getResponseBody() { return null; }
            @Override public String getRequestData() { return null; }
            @Override public Operation addHeader(String key, String value) { return this; }
            @Override public Operation removeHeader(String key) { return this; }
            @Override public String getHeader(String key) { return null; }
        };
        SampleParam param = new SampleParam();
        String result = op.replacePathVariables("/items/:id/:slug", param);
        assertThat(result).isEqualTo("/items/123/abc");
    }
}
