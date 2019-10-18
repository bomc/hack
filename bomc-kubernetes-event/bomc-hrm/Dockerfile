# ____________________________________________________
# docker build -t bomc/bomc-hrm:v1.0.0 .
# docker run -it -d -p 8080:8080 -p 9090:9090 --name bomc-hrm -h bomc-hrm bomc/bomc-hrm:latest
# ----------------------------------------------------

#FROM bomc/openjdk:v11
FROM bomc/java:v1.8.0

LABEL maintainer="bomc@bomc.org"

# I added a VOLUME pointing to "/tmp" because that is where a Spring Boot application creates working 
# directories for Tomcat by default. The effect is to create a temporary file on your host under 
# "/var/lib/docker" and link it to the container under "/tmp". This step is optional for the simple app 
# that I wrote here, but can be necessary for other Spring Boot applications if they need to actually 
# write in the filesystem.
VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=./target/hrm-1.0.0-SNAPSHOT.jar
 
ADD ${JAR_FILE} hrm.jar

#CMD /usr/bin/java -Xmx400m -Xms400m -jar hrm.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=prod","-jar","/hrm.jar"]