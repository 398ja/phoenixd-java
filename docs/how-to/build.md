# Build

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
