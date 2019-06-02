# ____________________________________________________
# docker build -t bomc/kafka-wf .
# docker run -it -d -p 8080:8080 -p 9090:9090 --name kafka-wf -h kafka-wf bomc/kafka-wf:latest
# ----------------------------------------------------

FROM bomc/wildfly:v13.0.0

COPY ./target/kafka-poc.war ${DEPLOYMENT_DIR}
 
ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh --server-config=${WILDFLY_CONFIG_FILE} -b=0.0.0.0 -bmanagement=0.0.0.0 -Djboss.node.name=local.docker -Dee8.preview.mode=true
