# phoenixd-java

A simple Java client for ACINQ's [phoenixd REST API](https://phoenix.acinq.co/server/api). It wraps the HTTP endpoints and exposes typed requests and responses.

## Installation
Artifacts are published to a Reposilite server. Add the repository to your Maven configuration to resolve the artifacts:

```xml
<repositories>
  <repository>
    <id>reposilite-releases</id>
    <url>https://maven.398ja.xyz/releases</url>
  </repository>
</repositories>

<dependency>
  <groupId>xyz.tcheeric</groupId>
  <artifactId>phoenixd-rest</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Requirements
- Java 21
- Maven
- A running instance of `phoenixd`
- A valid Lightning address (for test runs)

## Docker
Build and publish the `phoenixd-rest` and `phoenixd-mock` container images with the Jib Maven plugin:

```bash
./mvnw deploy -pl phoenixd-rest -am
./mvnw deploy -pl phoenixd-mock -am
```

Images are uploaded to `docker.398ja.xyz/phoenixd-rest` and `docker.398ja.xyz/phoenixd-mock` and tagged with both the project
version and `latest`, allowing consumers to pull the most recent build without specifying a version. See
[Docker](docs/how-to/docker.md) for details.

## Documentation
- Reference: [phoenixd REST API](https://phoenix.acinq.co/server/api)
- How-to guides:
  - [Build](docs/how-to/build.md)
  - [Test](docs/how-to/test.md)
  - [Docker](docs/how-to/docker.md)
  - [Code coverage](docs/how-to/coverage.md)
  - [Release](docs/how-to/release.md)
