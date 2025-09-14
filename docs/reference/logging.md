# Logging

phoenixd-rest and phoenixd-base use SLF4J for logging with Lombok's `@Slf4j`.
This lets your application choose the logging backend (Logback, Log4j2, slf4j-simple, etc.).

## Quick Start (slf4j-simple)

If your app does not already have a logging backend, add:

```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-simple</artifactId>
  <version>${slf4j.version}</version>
</dependency>
```

The module ships a `simplelogger.properties` that sets:

- `org.slf4j.simpleLogger.defaultLogLevel=info`
- `org.slf4j.simpleLogger.log.xyz.tcheeric=debug`
- date/time and thread name output

Override levels at runtime with system properties, for example:

```bash
JAVA_TOOL_OPTIONS="-Dorg.slf4j.simpleLogger.defaultLogLevel=info \
  -Dorg.slf4j.simpleLogger.log.xyz.tcheeric=debug"
```

## Spring Boot (Logback)

Add to `application.properties`:

```
logging.level.xyz.tcheeric=DEBUG
```

Or to `logback-spring.xml`:

```xml
<configuration>
  <logger name="xyz.tcheeric" level="DEBUG"/>
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

## What’s Logged

- HTTP requests: method, URI, timeout, status; error responses include a truncated body.
- Configuration resolution: whether values come from ENV, system properties, or `app.properties` (base module).
- Request factory decisions: Lightning address vs BOLT11 detection.

Sensitive values (e.g. Authorization) are redacted.

