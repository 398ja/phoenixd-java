package xyz.tcheeric.phoenixd.common.rest.util;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@RequiredArgsConstructor
@Data
public class Configuration {

    private final @NonNull String prefix;
    private final Properties properties;

    private String envKey(@NonNull String key) {
        return (prefix + "_" + key).toUpperCase().replace('.', '_');
    }

    private String envValue(@NonNull String key) {
        String value = System.getenv(envKey(key));
        return (value == null || value.isEmpty()) ? null : value;
    }

    private String resolve(@NonNull String key) {
        String env = envValue(key);
        if (env != null) return env;
        String sys = System.getProperty(prefix + "." + key);
        if (sys != null && !sys.isEmpty()) return sys;
        return properties.getProperty(prefix + "." + key);
    }

    @SneakyThrows
    public Configuration(@NonNull String prefix, @NonNull URL fileUrl) {
        this.prefix = prefix;
        this.properties = new Properties();
        try (InputStream in = fileUrl.openStream()) {
            if (in != null) {
                this.properties.load(in);
            }
        }
    }

    @SneakyThrows
    public Configuration(@NonNull String prefix) {
        this.prefix = prefix;
        this.properties = new Properties();
        // Try thread context classloader first (works well in app servers and Spring Boot)
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        URL resource = (tccl != null) ? tccl.getResource("app.properties") : null;
        if (resource == null) {
            // Fallback to this class' loader
            resource = getClass().getResource("/app.properties");
        }
        if (resource != null) {
            try (InputStream in = resource.openStream()) {
                if (in != null) {
                    this.properties.load(in);
                }
            }
        }
    }

    public List<String> keys() {
        List<String> result = new ArrayList<>();
        Set<String> names = properties.stringPropertyNames();
        String prefixDot = prefix + ".";
        for (String key : names) {
            if (key.startsWith(prefixDot)) {
                result.add(key.substring(prefixDot.length()));
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
