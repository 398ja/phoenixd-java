# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*
COPY --from=build /build/target/phoenixd-rest-*.jar /app/app.jar
RUN chown -R /app
EXPOSE 9740
ENTRYPOINT ["java","-jar","/app/app.jar"]
