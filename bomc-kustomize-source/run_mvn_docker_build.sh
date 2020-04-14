#!/bin/bash
eval $(minikube docker-env)

mvn clean install -Dmaven.test.skip=true && docker build -t bomc/ping:v1.0.0 .
