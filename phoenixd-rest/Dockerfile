FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/phoenixd-rest-1.0-SNAPSHOT.jar /app/phoenixd-rest.jar

EXPOSE 9740

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar phoenixd-rest.jar"]
