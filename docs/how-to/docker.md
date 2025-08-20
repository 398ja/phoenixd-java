# Docker

Build and run the `phoenixd-rest` module in a container after packaging the project:

```bash
mvn -q -pl phoenixd-rest -am package
docker build -t phoenixd-rest ./phoenixd-rest
docker run -p 9740:9740 phoenixd-rest
```

The image uses Java 21 and exposes port `9740`.
