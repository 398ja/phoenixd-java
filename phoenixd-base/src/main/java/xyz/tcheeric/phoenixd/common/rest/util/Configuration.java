package xyz.tcheeric.phoenixd.common.rest.util;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Configuration {

    private final @NonNull String prefix;
    private final PropertiesConfiguration properties;

    private String envKey(@NonNull String key) {
        return (prefix + "_" + key).toUpperCase().replace('.', '_');
    }

    private String envValue(@NonNull String key) {
        String value = System.getenv(envKey(key));
        return (value == null || value.isEmpty()) ? null : value;
    }

    private String resolve(@NonNull String key) {
        String env = envValue(key);
        if (env != null) {
            return env;
        }
        return properties.getString(prefix + "." + key, null);
    }

    @SneakyThrows
    public Configuration(@NonNull String prefix, @NonNull URL fileUrl) {
        this.prefix = prefix;
            this.properties = new Configurations().properties(fileUrl);
    }

    @SneakyThrows
    public Configuration(@NonNull String prefix) {
        this.prefix = prefix;
        this.properties = new Configurations().properties(getClass().getResource("/app.properties"));
    }

    public List<String> keys() {
        List<String> result = new ArrayList<>();
        Iterator<String> keysIterator = properties.getKeys(prefix);
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            if (key.startsWith(prefix + ".")) {
                String strippedKey = key.substring(prefix.length() + 1); // +1 for the dot
                result.add(strippedKey);
            }
        }
        return result;
    }

    public String get(@NonNull String key) {
        return resolve(key);
    }

    public int getInt(@NonNull String key, int defaultValue) {
        String value = resolve(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    public Long getLong(@NonNull String key) {
        String value = resolve(key);
        return value != null ? Long.valueOf(value) : Long.MIN_VALUE;
    }

    public Long getLong(@NonNull String key, Long defaultValue) {
        String value = resolve(key);
        return value != null ? Long.valueOf(value) : defaultValue;
    }

    public Double getDouble(@NonNull String key) {
        String value = resolve(key);
        return value != null ? Double.valueOf(value) : Double.MIN_VALUE;
    }

    public Double getDouble(@NonNull String key, Double defaultValue) {
        String value = resolve(key);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

    public Integer getInt(@NonNull String key) {
        String value = resolve(key);
        return value != null ? Integer.valueOf(value) : Integer.MIN_VALUE;
    }

    public boolean getBoolean(@NonNull String key) {
        String value = resolve(key);
        return value != null ? Boolean.parseBoolean(value) : false;
    }

    public String get(@NonNull String key, @NonNull String defaultValue) {
        String value = resolve(key);
        return value != null ? value : defaultValue;
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        String value = resolve(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}