# ____________________________________________________
# mvn clean install && docker build -t bomc/bomc-invoice:v1.0.0 .
# ----------------------------------------------------

FROM java:openjdk-8-jdk

ADD ./target/bomc-invoice-thorntail.jar /opt/bomc-invoice-thorntail.jar

EXPOSE 8080

# USER root

# ENTRYPOINT ["java", "-jar", "/opt/bomc-invoice-thorntail.jar", "-Dswarm.bind.address=127.0.0.1", "-Dswarm.port.offset=100", "-Djava.net.preferIPv4Stack=true"]
ENTRYPOINT ["java", "-jar", "/opt/bomc-invoice-thorntail.jar", "-Djava.net.preferIPv4Stack=true"]