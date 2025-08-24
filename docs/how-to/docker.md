# Docker

The `phoenixd-rest` module can be containerized and published to `docker.398ja.xyz`
using the [Jib Maven plugin](https://github.com/GoogleContainerTools/jib).

Build and push the image during the Maven `deploy` phase:

```bash
./mvnw deploy -pl phoenixd-rest -am
```

Jib will publish `docker.398ja.xyz/phoenixd-rest:<version>`.

To run the pushed image locally:

```bash
docker run -p 9740:9740 docker.398ja.xyz/phoenixd-rest:<version>
```

The image uses Java 21 and exposes port `9740`.
