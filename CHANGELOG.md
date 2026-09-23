# Changelog

## 0.3.1 — 2026-09-23

### Fixed
- **The quote PATCH is verified rather than assumed, and no longer races the payment webhook.**
  Settling an invoice logged `Quote updated to PAID ... status=200` whenever the PATCH returned
  2xx, without ever re-reading the row. On 2026-09-23 it logged exactly that for three staging
  sales whose quotes were still `PENDING`, and the mint refused to issue against all three
  (398ja/payment-adapter#245).

  A 200 from a Spring Data REST PATCH does not mean the field changed: an unwritable field
  answers 200, and so does a lost update under the entity's optimistic-locking `version` column.
  The state is now read back with a fresh GET and checked; a write that is accepted without
  landing logs `QUOTE UPDATE LOST` and says what the consequence will be.

  The call is also synchronous now. The caller posts the payment webhook immediately afterwards,
  and payment-adapter was observed handling that webhook for the same quote in the same
  millisecond the PATCH landed. Completing this write first removes the overlap rather than
  narrowing it.

  The underlying clobber was in payment-adapter and is fixed there in 0.16.2. This change does
  not fix it — it makes a recurrence name itself in seconds instead of costing an afternoon of
  log archaeology.

- **The settle path's HTTP calls are bounded by a per-request timeout.** Those calls are now
  blocking and run on a two-thread scheduler. `connectTimeout` does not bound a server that
  accepts a connection and never answers, so two stuck requests would have wedged auto-settlement
  for every invoice — the mock would quietly stop settling and look like the gateway had failed.

### Fixed (tests)
- **Two assertions had drifted from the endpoint they describe**, leaving this module's suite red
  since mockpay learned to accept an `externalId`: the 404 body echoes `lookupKey` rather than
  `paymentHash`, and the 400 message names both parameters. A suite that is always red trains
  everyone to skip it, and it hid that other changes were landing unverified. 31 tests green,
  which is the first time for this module.

## 0.3.0 — 2026-08-29

### Fixed
- **phoenixd-mock posts the payment webhook, not only the quote PATCH.** Settling an
  invoice marked the gateway quote PAID without exercising the webhook that carries the
  news to the mint, so a mint could sit on an unfunded quote while the quote read PAID.
  The mock now also POSTs the real form-encoded callback to `/webhook/phoenixd`, so a
  staging stack exercises the same path production does.

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
## 0.1.4 — 2025-09-13

### Enhancements
- Add default logging configuration for SLF4J Simple in phoenixd-rest (`simplelogger.properties`).
- Document logging usage and configuration.

