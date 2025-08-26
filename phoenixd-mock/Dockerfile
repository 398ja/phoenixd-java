# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/phoenixd-mock-*.jar /app/app.jar
RUN chown -R /app
EXPOSE 9740
ENTRYPOINT ["java","-jar","/app/app.jar"]

