# Changelog

## 0.1.1 — 2025-09-13

### Highlights
- Fixes “URI with undefined scheme” when `phoenixd.base_url` lacks an HTTP scheme by defaulting to `http://`.
- Centralizes configuration via shared `Configuration` util (env vars take precedence over `app.properties`).

### Changes
- fix: normalize base_url scheme and use shared Configuration
  - F:phoenixd-rest/src/main/java/xyz/tcheeric/phoenixd/operation/AbstractOperation.java
  - F:phoenixd-rest/src/test/java/xyz/tcheeric/phoenixd/operation/BaseUrlNormalizationTest.java
  - F:phoenixd-test/src/test/java/xyz/tcheeric/phoenixd/test/TestUtils.java
- docs: document scheme defaulting for `phoenixd.base_url`
  - F:docs/reference/configuration.md
- build: bump version to 0.1.1
  - F:pom.xml
  - F:phoenixd-rest/pom.xml
  - F:phoenixd-test/pom.xml
  - F:phoenixd-base/pom.xml
  - F:phoenixd-model/pom.xml
  - F:phoenixd-mock/pom.xml

### Behavior Notes
- `phoenixd.base_url` without a scheme now resolves as `http://...`.
- If `phoenixd.base_url` is unset or blank, an `IllegalArgumentException` is thrown during request construction.

### Configuration
- Env vars (preferred): `PHOENIXD_USERNAME`, `PHOENIXD_PASSWORD`, `PHOENIXD_BASE_URL`, `PHOENIXD_TIMEOUT`.
- Or `app.properties` on the classpath with `phoenixd.username`, `phoenixd.password`, `phoenixd.base_url`, `phoenixd.timeout`.

### Testing
- Command: `mvn -q verify`
- Note: In restricted sandboxes, tests that open loopback sockets (MockWebServer) may fail with `SocketException: Operation not permitted`. In a normal dev/CI environment, the test suite is expected to pass.

### API Changes
- None.

### Security
- No new dependencies; no changes to security-sensitive logic.

### Migration
- No breaking changes. If you relied on rejecting schemeless base URLs, be aware they now default to `http://`.
## 0.1.2 — 2025-09-13

### Fixes
- Add explicit `commons-lang3` runtime dependency required by `commons-configuration2` to prevent `NoClassDefFoundError: org/apache/commons/lang3/SystemProperties` in consumer apps.

### Notes
- If you previously consumed `0.1.1`, update to `0.1.2` to pick up the dependency fix. Dependency metadata for `0.1.1` may be cached by your build; a version bump ensures the fix is resolved.
## 0.1.3 — 2025-09-13

### Enhancements
- Add structured logging via Lombok `@Slf4j` in core components.
  - Logs config resolution source (ENV, system property, file) and file load location.
  - Logs HTTP requests (method, URI, timeout) and responses (status), with safe truncation and redaction.
- Improve configuration discovery in fat jars/containers: try TCCL first, then class loader; support JVM system properties.

### Notes
- Enable debug logs with `logging.level.xyz.tcheeric=DEBUG` to see detailed request and configuration traces.

