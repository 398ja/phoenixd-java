FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/phoenixd-rest-*.jar /app/phoenixd-rest.jar

EXPOSE 9740

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "phoenixd-rest.jar"]
