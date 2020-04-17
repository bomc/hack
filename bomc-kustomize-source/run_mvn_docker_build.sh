#!/bin/bash
eval $(minishift docker-env)

mvn clean install -Dmaven.test.skip=true && docker build -t 172.30.1.1:5000/bomc/ping:v1.0.7 .
