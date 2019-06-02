# ____________________________________________________
# docker build -t bomc/${artifactId}:v1.0.0 .
# docker run -it -d -p 8080:8080 -p 9090:9090 --name ${artifactId} -h ${artifactId} bomc/${artifactId}:latest
# ----------------------------------------------------

FROM bomc/wildfly:v13.0.0

COPY ./target/${artifactId}.war ${DEPLOYMENT_DIR}

ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh --server-config=${WILDFLY_CONFIG_FILE} -b=0.0.0.0 -bmanagement=0.0.0.0 -Djboss.node.name=local.${artifactId} -Dee8.preview.mode=true
