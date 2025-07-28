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

This will compile all modules and execute the unit tests. The tests expect a `phoenixd` instance running locally and some configuration values defined in `phoenixd-test/src/test/resources/app.properties`.


Before running the tests, update the `test.pay_lnaddress` entry in that file so it targets a Lightning address that you control.

## Testing
Run the full test suite from the repository root:

```bash
mvn -q verify
```

This command executes unit and integration tests for all modules.

## Code coverage
Running `mvn clean verify` generates a Jacoco coverage report. The aggregated HTML report is written to `target/site/jacoco-aggregate/index.html`.

## Usage example
```java
Configuration cfg = new Configuration("phoenixd");
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

