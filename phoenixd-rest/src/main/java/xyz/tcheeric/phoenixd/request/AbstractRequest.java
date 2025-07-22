package xyz.tcheeric.phoenixd.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import xyz.tcheeric.common.rest.Operation;
import xyz.tcheeric.common.rest.Request;
import xyz.tcheeric.common.rest.Response;
import xyz.tcheeric.common.rest.VoidResponse;
import xyz.tcheeric.phoenixd.model.response.GetLightningAddressResponse;

import java.lang.reflect.ParameterizedType;

@Data
@RequiredArgsConstructor
public abstract class AbstractRequest<T extends Request.Param, U extends Response> implements Request<T, U> {
    private final String path;
    private final Param param;
    private final Operation operation;

    @SneakyThrows
    @Override
    public U getResponse() {
        Class<U> responseType = getResponseType();
        if (VoidResponse.class.isAssignableFrom(responseType)) {
            return (U) new VoidResponse();
        }
        String body = this.getOperation().execute().getResponseBody();

        if(GetLightningAddressResponse.class.equals(responseType)) {
            // Remove the ₿ prefix
            if(body.charAt(0) == '₿') {
                return (U) new GetLightningAddressResponse(body.substring(1));
            } else {
                return (U) new GetLightningAddressResponse(body);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, responseType);
    }

    private Class<U> getResponseType() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<U>) superClass.getActualTypeArguments()[1];
    }
}