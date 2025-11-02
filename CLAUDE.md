# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

phoenixd-java is a Java 21 client library for ACINQ's phoenixd REST API. It wraps HTTP endpoints for Lightning Network operations (invoices, payments, address handling) into typed Java requests and responses.

## Maven Commands

### Build & Test
```bash
# Clean build with unit tests
mvn clean install

# Run all tests (unit + integration)
mvn -q verify

# Build specific module
mvn clean install -pl phoenixd-rest -am
```

### Test Individual Modules
```bash
# Test specific module
mvn test -pl phoenixd-rest

# Run single test class
mvn test -pl phoenixd-rest -Dtest=ClassName

# Run single test method
mvn test -pl phoenixd-rest -Dtest=ClassName#methodName
```

### Code Coverage
```bash
# Generate coverage report (requires mvn verify)
mvn clean verify

# View aggregated report at:
# target/site/jacoco-aggregate/index.html

# Coverage threshold: 80% (enforced by jacoco-maven-plugin)
```

### Docker Build (Jib)
```bash
# Build and publish phoenixd-rest container
./mvnw deploy -pl phoenixd-rest -am

# Build and publish phoenixd-mock container
./mvnw deploy -pl phoenixd-mock -am

# Images are pushed to docker.398ja.xyz with version and 'latest' tags
```

## Module Architecture

The project is a multi-module Maven build with a layered architecture:

```
phoenixd-java (parent)
├── phoenixd-base       # Core abstractions and configuration
├── phoenixd-model      # Request params and response DTOs
├── phoenixd-rest       # HTTP client implementation
├── phoenixd-mock       # Mock server for testing
└── phoenixd-test       # Integration tests
```

### Module Dependencies
- **phoenixd-base**: Foundation layer with `Request`, `Operation`, `Response` interfaces and `Configuration` utility
- **phoenixd-model**: Depends on phoenixd-base; defines all param/response POJOs (e.g., `CreateInvoiceParam`, `PayInvoiceResponse`)
- **phoenixd-rest**: Depends on phoenixd-model and phoenixd-base; implements abstract operations (GET, POST, etc.) and concrete request classes
- **phoenixd-test**: Depends on phoenixd-rest; runs integration tests against a live phoenixd instance
- **phoenixd-mock**: Standalone mock server; no dependencies on other modules

### Request-Operation Pattern

The library uses a two-layer pattern:
1. **Request** layer (`AbstractRequest` + concrete implementations in `phoenixd-rest/request/impl/rest/`):
   - Takes a `Param` object and an `Operation`
   - Calls `operation.execute()` and deserializes the response body into a typed response object
   - Example: `CreateBolt11InvoiceRequest`, `PayLightningAddressRequest`

2. **Operation** layer (`AbstractOperation` + HTTP method implementations in `phoenixd-rest/operation/impl/`):
   - Handles HTTP mechanics: building URIs, auth headers (Basic Auth), sending requests via `HttpClient`
   - Replaces path variables (e.g., `{invoice}`) from `Param` fields
   - Returns raw response body as string
   - Example: `GetOperation`, `PostOperation`

### PayRequestFactory
Located in `phoenixd-rest/request/impl/rest/PayRequestFactory.java`, this factory detects whether a payment string is:
- A Lightning address (contains `@`)
- A BOLT11 invoice (matches `^(lnbc|lntb|lnsb|lnbcrt)[0-9]*[a-z0-9]+$` case-insensitive)

Returns the appropriate `BasePayRequest` subclass.

## Configuration

Tests require a running phoenixd instance and the following environment variables:
```bash
PHOENIXD_USERNAME=...
PHOENIXD_PASSWORD=...
PHOENIXD_BASE_URL=http://localhost:9740  # or your phoenixd URL
```

Additional test-specific config lives in `phoenixd-test/src/test/resources/app.properties` (e.g., `test.pay_lnaddress`).

Configuration resolution follows: **ENV > system properties > app.properties**. The `Configuration` class in phoenixd-base handles this.

## Logging

Uses SLF4J with Lombok's `@Slf4j`. phoenixd-rest ships `simplelogger.properties` with:
- Default log level: INFO
- `xyz.tcheeric` package: DEBUG
- Date/time and thread name enabled

Override at runtime:
```bash
JAVA_TOOL_OPTIONS="-Dorg.slf4j.simpleLogger.defaultLogLevel=info -Dorg.slf4j.simpleLogger.log.xyz.tcheeric=debug"
```

Or for Spring Boot apps, set in `application.properties`:
```
logging.level.xyz.tcheeric=DEBUG
```

**Logged events**:
- HTTP request/response (method, URI, status, timeout)
- Configuration resolution (where values came from)
- Payment request detection logic (PayRequestFactory)
- Sensitive headers (Authorization) are redacted

## Code Style & Conventions

From `.github/copilot-instructions.md`:
- Commit messages follow `type: description` format (e.g., `fix: handle null node`, `feat: add invoice decoding`)
- Use present tense verbs for the description
- Breaking changes must be flagged with **BREAKING** in the PR/commit message
- All new code should have test coverage
- Use Lombok annotations (`@Data`, `@Slf4j`, `@NonNull`, `@SneakyThrows`) consistently

## Testing Notes

- Unit tests use JUnit 5 and AssertJ
- phoenixd-rest uses MockWebServer (OkHttp) for HTTP mocking
- Integration tests in phoenixd-test require a live phoenixd instance
- When tests fail, check that env vars are set and phoenixd is reachable
