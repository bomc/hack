# ____________________________________________________
# mvn clean install && docker build -t bomc/bomc-invoice:v1.0.0 .
# ----------------------------------------------------

FROM java:openjdk-8-jdk

ADD ./target/bomc-invoice-thorntail.jar /opt/bomc-invoice-thorntail.jar
ADD ./configuration/standalone.xml /opt/standalone.xml
EXPOSE 8080

# USER root

# ENTRYPOINT ["java", "-jar", "/opt/bomc-invoice-thorntail.jar", "-Dthorntail.bind.address=127.0.0.1", "-Dthorntail.port.offset=100", "-Djava.net.preferIPv4Stack=true", "-Dthorntail.project.stage.file=/project-kubernetes.yml"]
ENTRYPOINT ["java", "-jar", "/opt/bomc-invoice-thorntail.jar", "-Djava.net.preferIPv4Stack=true", "-Skubernetes", "-copt/standalone.xml", "-e BASE_URL=/bomc-invoice"]