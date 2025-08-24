FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/phoenixd-mock-*.jar /app/phoenixd-mock.jar

EXPOSE 9740

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "phoenixd-mock.jar"]
