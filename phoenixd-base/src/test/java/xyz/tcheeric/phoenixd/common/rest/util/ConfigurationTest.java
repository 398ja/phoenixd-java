package xyz.tcheeric.phoenixd.common.rest.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        URL url = getClass().getResource("/test-config.properties");
        configuration = new Configuration("test", url);
    }

    // Verifies all getter methods return expected defaults or file values
    @Test
    void keysAndGettersHandleDefaultsProperly() {
        assertThat(configuration.keys()).containsExactlyInAnyOrder("string", "int", "long", "double", "boolean");

        assertThat(configuration.get("string")).isEqualTo("fromFile");
        assertThat(configuration.get("missing", "fallback")).isEqualTo("fallback");
        assertThat(configuration.get("string", "other")).isEqualTo("fromFile");

        assertThat(configuration.getInt("int")).isEqualTo(123);
        assertThat(configuration.getInt("missingInt")).isEqualTo(Integer.MIN_VALUE);
        assertThat(configuration.getInt("missingInt", 7)).isEqualTo(7);
        assertThat(configuration.getInt("int", 7)).isEqualTo(123);

        assertThat(configuration.getLong("long")).isEqualTo(4567890123L);
        assertThat(configuration.getLong("missingLong")).isEqualTo(Long.MIN_VALUE);
        assertThat(configuration.getLong("missingLong", 99L)).isEqualTo(99L);
        assertThat(configuration.getLong("long", 88L)).isEqualTo(4567890123L);

        assertThat(configuration.getDouble("double")).isEqualTo(3.14d);
        assertThat(configuration.getDouble("missingDouble")).isEqualTo(Double.MIN_VALUE);
        assertThat(configuration.getDouble("missingDouble", 2.5d)).isEqualTo(2.5d);
        assertThat(configuration.getDouble("double", 1.1d)).isEqualTo(3.14d);

        assertThat(configuration.getBoolean("boolean")).isTrue();
        assertThat(configuration.getBoolean("missingBool")).isFalse();
        assertThat(configuration.getBoolean("missingBool", true)).isTrue();
        assertThat(configuration.getBoolean("boolean", false)).isTrue();
    }

    // Ensures the default constructor loads properties from the classpath
    @Test
    void defaultConstructorLoadsAppProperties() {
        Configuration cfg = new Configuration("test");
        assertThat(cfg.get("string")).isEqualTo("fromFile");
    }

    // Confirms environment variables take precedence over property files
    @Test
    void environmentVariablesOverrideProperties() throws Exception {
        String classpath = System.getProperty("java.class.path");
        ProcessBuilder builder = new ProcessBuilder("java", "-cp", classpath, EnvReader.class.getName());
        builder.environment().put("TEST_STRING", "fromEnv");
        builder.environment().put("TEST_INT", "999");
        Process process = builder.start();
        byte[] bytes = process.getInputStream().readAllBytes();
        int exit = process.waitFor();
        assertThat(exit).isZero();
        String[] lines = new String(bytes, StandardCharsets.UTF_8).trim().split("\\R");
        assertThat(lines).containsExactly("fromEnv", "999");
    }

    public static class EnvReader {
        public static void main(String[] args) {
            Configuration config = new Configuration("test", EnvReader.class.getResource("/test-config.properties"));
            System.out.println(config.get("string"));
            System.out.println(config.getInt("int"));
        }
    }
}
