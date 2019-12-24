#!/bin/bash
eval $(minikube docker-env)

mvn clean install -PskipTest && docker build -t bomc/bomc-hrm:v1.0.0 .
