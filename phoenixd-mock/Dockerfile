# Runtime stage - uses pre-built JAR
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY phoenixd-mock/target/phoenixd-mock-*.jar /app/app.jar
EXPOSE 9740

# No healthcheck - phoenixd-mock doesn't have a /getinfo endpoint

ENTRYPOINT ["java","-jar","/app/app.jar"]

