package xyz.tcheeric.phoenixd.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tcheeric.phoenixd.common.rest.Operation;
import xyz.tcheeric.phoenixd.common.rest.Request;
import xyz.tcheeric.phoenixd.common.rest.Response;
import xyz.tcheeric.phoenixd.common.rest.VoidResponse;
import xyz.tcheeric.phoenixd.model.response.GetLightningAddressResponse;

import java.lang.reflect.ParameterizedType;

@Data
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractRequest<T extends Request.Param, U extends Response> implements Request<T, U> {
    private final String path;
    private final T param;
    private final Operation operation;

    @SneakyThrows
    @Override
    public U getResponse() {
        Class<U> responseType = getResponseType();
        if (log.isDebugEnabled()) {
            log.debug("Executing request: path={}, paramClass={}, responseType={}", path, param != null ? param.getClass().getSimpleName() : "<none>", responseType.getSimpleName());
        }
        if (VoidResponse.class.isAssignableFrom(responseType)) {
            if (log.isDebugEnabled()) log.debug("VoidResponse detected; skipping network call for path={}", path);
            return (U) new VoidResponse();
        }
        String body = this.getOperation().execute().getResponseBody();
        if (log.isDebugEnabled()) {
            String preview = body == null ? "" : (body.length() > 256 ? body.substring(0, 256) + "..." : body);
            log.debug("Received response body (truncated): {}", preview);
        }

        if(GetLightningAddressResponse.class.equals(responseType)) {
            // Hack: Remove the ₿ prefix
            if(body != null && !body.isEmpty() && body.charAt(0) == '₿') {
                if (log.isDebugEnabled()) log.debug("Stripping leading '₿' from lightning address response");
                return (U) new GetLightningAddressResponse(body.substring(1));
            } else {
                return (U) new GetLightningAddressResponse(body);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        if (log.isDebugEnabled()) log.debug("Deserializing JSON into {}", responseType.getSimpleName());
        return objectMapper.readValue(body, responseType);
    }

    private Class<U> getResponseType() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<U>) superClass.getActualTypeArguments()[1];
    }
}
