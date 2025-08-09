package xyz.tcheeric.phoenixd.common.rest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestParamTest {

    @Test
    void defaultKindIsPath() {
        Request.Param param = new Request.Param() {};
        assertThat(param.getKind()).isEqualTo(Request.Param.Kind.PATH);
    }

    @Test
    void enumValuesAndVoidClasses() {
        assertThat(Request.Param.Kind.valueOf("QUERY")).isEqualTo(Request.Param.Kind.QUERY);
        assertThat(Request.Param.Kind.values()).containsExactly(Request.Param.Kind.PATH, Request.Param.Kind.QUERY);
        assertThat(new VoidResponse()).isNotNull();
        assertThat(new VoidRequestParam()).isNotNull();
    }
}
