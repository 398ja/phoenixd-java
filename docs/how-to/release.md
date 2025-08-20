# Release

Tag a commit with `v*` to trigger the release workflow. The workflow builds the JARs with `mvn -q package`, publishes them to GitHub Packages, and pushes a Docker image built from `phoenixd-rest/Dockerfile` to the GitHub Container Registry.
