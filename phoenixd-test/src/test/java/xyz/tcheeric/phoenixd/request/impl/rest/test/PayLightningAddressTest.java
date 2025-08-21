package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.common.rest.util.Configuration;
import xyz.tcheeric.phoenixd.model.param.PayLightningAddressParam;
import xyz.tcheeric.phoenixd.model.response.PayLightningAddressInvoiceResponse;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;
import xyz.tcheeric.phoenixd.request.impl.rest.PayLightningAddressRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;
import xyz.tcheeric.phoenixd.test.TestUtils;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(LocalTestServerExtension.class)
public class PayLightningAddressTest {

    // Validates the request sends correct data and parses the payment response
    @Test
    public void testConstructor() {
        // Arrange
        Configuration configuration = new Configuration("test", getClass().getResource("/app.properties"));
        PayLightningAddressParam payLightningAddressParam = new PayLightningAddressParam();
        payLightningAddressParam.setAddress(configuration.get("pay_lnaddress"));
        payLightningAddressParam.setMessage("test message: testConstructor" + System.currentTimeMillis());
        payLightningAddressParam.setAmountSat(Integer.valueOf(configuration.get("pay_amountSat")));
        PayLightningAddressRequest payLightningAddressRequest = new PayLightningAddressRequest(payLightningAddressParam);

        // Act
        PayLightningAddressInvoiceResponse response = payLightningAddressRequest.getResponse();

        // Assert
        assertEquals("/paylnaddress", payLightningAddressRequest.getPath());
        assertEquals(PostOperation.class, payLightningAddressRequest.getOperation().getClass());
        assertEquals(payLightningAddressParam.toString(), payLightningAddressRequest.getOperation().getRequestData());
        assertEquals(10, response.getRecipientAmountSat());
    }

    // Checks default URI and headers on the built request
    @Test
    public void testUriAndHeaders() {
        PayLightningAddressParam param = new PayLightningAddressParam();
        param.setAddress("398ja@strike.me");
        param.setMessage("msg");
        param.setAmountSat(10);
        PayLightningAddressRequest request = new PayLightningAddressRequest(param);

        assertEquals("http://localhost:9740/paylnaddress", request.getOperation().getHttpRequest().uri().toString());
        assertEquals("Basic Og==", request.getOperation().getHeader("Authorization"));
        assertEquals("application/x-www-form-urlencoded", request.getOperation().getHeader("Content-Type"));
    }

    // Ensures IOExceptions are raised when the server responds with an error
    @Test
    public void testErrorHandling() throws Exception {
        HttpServer errorServer = HttpServer.create(new InetSocketAddress(9751), 0);
        errorServer.createContext("/paylnaddress", exchange -> {
            byte[] bytes = "error".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        errorServer.start();
        try {
            TestUtils.setBaseUrl("http://localhost:" + ERROR_SERVER_PORT);
            PayLightningAddressParam param = new PayLightningAddressParam();
            param.setAddress("398ja@strike.me");
            param.setMessage("msg");
            param.setAmountSat(10);
            PayLightningAddressRequest request = new PayLightningAddressRequest(param);
            assertThrows(IOException.class, request::getResponse);
        } finally {
            errorServer.stop(0);
            TestUtils.setBaseUrl("http://localhost:9740");
        }
    }

}
