package xyz.tcheeric.phoenixd.operation;

import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import xyz.tcheeric.phoenixd.operation.impl.PostOperation;
import xyz.tcheeric.phoenixd.common.rest.Operation;
import xyz.tcheeric.phoenixd.common.rest.Request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public abstract class AbstractOperation implements Operation {

    protected HttpRequest httpRequest;
    private String responseBody;
    private String requestData;

    private static PropertiesConfiguration properties;

    static {
        try {
            properties = new Configurations().properties(AbstractOperation.class.getResource("/app.properties"));
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public AbstractOperation(@NonNull HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @SneakyThrows
    public AbstractOperation(@NonNull String method, @NonNull String path, String requestData) {
        String username = getProperty("username");
        String password = ("password");
        String baseUrl = getProperty("base_url");
        long timeout =  properties.getLong("phoenixd.timeout");
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        this.requestData = requestData;

        HttpRequest.BodyPublisher bodyPublisher = requestData == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(requestData);

        this.httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Basic " + encodedAuth)
                .timeout(Duration.ofMillis(timeout))
                .method(method, bodyPublisher)
                .build();
    }

    @SneakyThrows
    public AbstractOperation(@NonNull String method, @NonNull String path, @NonNull Request.Param param, String requestData) {
        String username = getProperty("username");
        String password = getProperty("password");
        String baseUrl = getProperty("base_url");

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        long timeout = properties.getLong("phoenixd.timeout");

        this.requestData = requestData;

        HttpRequest.BodyPublisher bodyPublisher = requestData == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(requestData);

        String separator = param.getKind() == Request.Param.Kind.PATH ? "/" : "?";
        this.httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + replacePathVariables(path, param) + separator + param))
                .header("Authorization", "Basic " + encodedAuth)
                .timeout(Duration.ofMillis(timeout))
                .method(method, bodyPublisher)
                .build();
    }

    @SneakyThrows
    @Override
    public Operation execute() {
        CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder()
                .build()
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        this.responseBody = response.get().body();
        var statusCode = response.get().statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Failed to create invoice: " + statusCode + " " + responseBody);
        }
        return this;
    }
    @Override
    public Operation addHeader(@NonNull String key, @NonNull String value) {
        HttpHeaders headers = httpRequest.headers();
        Map<String, List<String>> headersMap = headers.map();
        Map<String, List<String>> newHeadersMap = headersMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        newHeadersMap.put(key, List.of(value));

        HttpRequest newHttpRequest = HttpRequest.newBuilder()
                .uri(httpRequest.uri())
                .timeout(httpRequest.timeout().orElse(null))
                .headers(newHeadersMap.entrySet().stream()
                        .flatMap(e -> e.getValue().stream().map(v -> Map.entry(e.getKey(), v)))
                        .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                        .toArray(String[]::new))
                .POST(httpRequest.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()))
                .build();

        this.setHttpRequest(newHttpRequest);

        return new PostOperation(newHttpRequest);
    }

    @Override
    public Operation removeHeader(String key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getHeader(@NonNull String key) {
        return httpRequest.headers().firstValue(key).orElse(null);
    }

    public String replacePathVariables(String path, Request.Param param) {
        return path;
    }

    private static String getProperty(String key) {
        return properties.getString("phoenixd." + key, null);
    }
}
