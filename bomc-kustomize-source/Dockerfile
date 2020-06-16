FROM adoptopenjdk/openjdk8:latest

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/ping-1.0.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-jar","/app.jar"]