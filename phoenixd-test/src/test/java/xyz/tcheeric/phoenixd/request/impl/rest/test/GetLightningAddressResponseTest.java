package xyz.tcheeric.phoenixd.request.impl.rest.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import xyz.tcheeric.phoenixd.model.response.GetLightningAddressResponse;
import xyz.tcheeric.phoenixd.request.impl.rest.GetLightningAddressRequest;
import xyz.tcheeric.phoenixd.test.LocalTestServerExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(LocalTestServerExtension.class)
public class GetLightningAddressResponseTest {

    @Test
    public void testConstructor() {
        // Arrange
        GetLightningAddressRequest getLightningAddressRequest = new GetLightningAddressRequest();

        // Act
        GetLightningAddressResponse response = getLightningAddressRequest.getResponse();

        // Assert
        assertEquals("/getlnaddress", getLightningAddressRequest.getPath());
        assertNull(getLightningAddressRequest.getOperation().getRequestData());
        assertNotNull(response.getLightningAddress());
    }

}
