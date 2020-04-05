#!/bin/bash
eval $(minikube docker-env)

mvn clean package -PskipTest && docker build -t bomc/ping:v1.0.0 .
