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
        return properties.getString(prefix + "." + key, null);
    }

    public int getInt(@NonNull String key, int defaultValue) {
        return properties.getInt(prefix + "." + key, defaultValue);
    }

    public Long getLong(@NonNull String key) {
        return properties.getLong(prefix + "." + key, Long.MIN_VALUE);
    }

    public Long getLong(@NonNull String key, Long defaultValue) {
        return properties.getLong(prefix + "." + key, defaultValue);
    }

    public Double getDouble(@NonNull String key) {
        return properties.getDouble(prefix + "." + key, Double.MIN_VALUE);
    }

    public Double getDouble(@NonNull String key, Double defaultValue) {
        return properties.getDouble(prefix + "." + key, defaultValue);
    }

    public Integer getInt(@NonNull String key) {
        return properties.getInt(prefix + "." + key, Integer.MIN_VALUE);
    }

    public boolean getBoolean(@NonNull String key) {
        return properties.getBoolean(prefix + "." + key, false);
    }

    public String get(@NonNull String key, @NonNull String defaultValue) {
        return properties.getString(prefix + "." + key, defaultValue);
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return properties.getBoolean(prefix + "." + key, defaultValue);
    }
}