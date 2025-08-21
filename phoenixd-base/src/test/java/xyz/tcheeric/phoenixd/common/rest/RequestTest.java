package xyz.tcheeric.phoenixd.common.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestTest {

    // Ensures the default getResponse implementation throws an exception
    @Test
    void defaultGetResponseThrowsUnsupportedOperationException() {
        Request<Request.Param, Response> request = new Request<Request.Param, Response>() {};
        assertThrows(UnsupportedOperationException.class, request::getResponse);
    }
}
