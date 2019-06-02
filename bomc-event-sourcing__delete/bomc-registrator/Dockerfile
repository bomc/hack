# ____________________________________________________
# docker build -t bomc/registrator-wf
# docker run -it -d -p 8080:8080 -p 9090:9090 --name registrator-wf -h registrator-wf bomc/registrator-wf:latest
# ----------------------------------------------------

FROM bomc/wildfly:v13.0.0

COPY ./target/bomc-registrator.war ${DEPLOYMENT_DIR}
 
ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh --server-config=${WILDFLY_CONFIG_FILE} -b=0.0.0.0 -bmanagement=0.0.0.0 -Djboss.node.name=local.registrator -Dee8.preview.mode=true
