# Runtime stage - uses pre-built JAR
FROM eclipse-temurin:21-jre

# Install curl for healthchecks
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY phoenixd-mock/target/phoenixd-mock-*.jar /app/app.jar
EXPOSE 9740

HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:9740/getinfo || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]

