# Release

Tag a commit with `v*` to trigger the release workflow. The workflow builds the JARs with `mvn -q package`, publishes them to the Reposilite server, and pushes a Docker image built from `phoenixd-rest/Dockerfile` to the GitHub Container Registry.

Artifacts are published using the Reposilite repository. The parent `pom.xml` defines the distribution and resolution repositories:

```xml
<distributionManagement>
  <repository>
    <id>reposilite-releases</id>
    <url>https://maven.398ja.xyz/releases</url>
  </repository>
  <snapshotRepository>
    <id>reposilite-snapshots</id>
    <url>https://maven.398ja.xyz/snapshots</url>
  </snapshotRepository>
</distributionManagement>

<repositories>
  <repository>
    <id>reposilite-releases</id>
    <url>https://maven.398ja.xyz/releases</url>
  </repository>
</repositories>
```

Ensure your `~/.m2/settings.xml` includes credentials for the `reposilite-releases` and `reposilite-snapshots` servers before tagging a release.
