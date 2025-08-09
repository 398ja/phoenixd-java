package xyz.tcheeric.phoenixd.common.rest.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void httpMethodConstantsMatchValues() {
        assertThat(Constants.HTTP_GET_METHOD).isEqualTo("GET");
        assertThat(Constants.HTTP_POST_METHOD).isEqualTo("POST");
        assertThat(Constants.HTTP_PUT_METHOD).isEqualTo("PUT");
        assertThat(Constants.HTTP_DELETE_METHOD).isEqualTo("DELETE");
        assertThat(Constants.HTTP_PATCH_METHOD).isEqualTo("PATCH");
    }
}
