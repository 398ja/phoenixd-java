# phoenixd-java

A simple Java client for ACINQ's [phoenixd REST API](https://phoenix.acinq.co/server/api). It wraps the HTTP endpoints and exposes typed requests and responses.

## Requirements
- Java 21
- Maven
- A running instance of `phoenixd`
- A valid Lightning address (for test runs)

## Building
Clone the repository and run the Maven build:

```bash
mvn clean install
```

This will compile all modules and execute the unit tests. The tests expect a `phoenixd` instance running locally and the following environment variables to be set:

```
PHOENIXD_USERNAME
PHOENIXD_PASSWORD
PHOENIXD_BASE_URL
```

Some test-specific values such as `test.pay_lnaddress` remain in `phoenixd-test/src/test/resources/app.properties`; update them as needed to target a Lightning address that you control.

## Testing
Run the full test suite from the repository root:

```bash
mvn -q verify
```

This command executes unit and integration tests for all modules.

## Code coverage
Running `mvn clean verify` generates a Jacoco coverage report. The aggregated HTML report is written to `target/site/jacoco-aggregate/index.html`.

## Release

Tag a commit with `v*` to trigger the release workflow. The workflow builds the JARs with `mvn -q package`, publishes them to GitHub Packages, and pushes a Docker image built from `phoenixd-rest/Dockerfile` to the GitHub Container Registry.


## Usage example
```java
CreateInvoiceParam param = new CreateInvoiceParam();
param.setAmountSat(100);
CreateBolt11InvoiceRequest req = new CreateBolt11InvoiceRequest(param);
CreateInvoiceResponse resp = req.getResponse();
```

## Contributing
Only a small subset of endpoints is implemented at the moment. Contributions are welcome! To add a new one you typically create:
1. A request parameter class extending `Request.Param` (in `phoenixd-model`)
2. A response class implementing `Response` (in `phoenixd-model`)
3. The request class itself (in `phoenixd-rest`)
4. Corresponding unit tests (in `phoenixd-test`)

### Supported endpoints
- `/createinvoice`
- `/decodeinvoice`
- `/getlnaddress`
- `/payinvoice`
- `/paylnaddress`

