#!/bin/bash

echo #################################################
echo Delete hrm microservice minikube:deployments
echo -------------------------------------------------


echo Delete hrm microservices
kubectl delete -f ./deployment/istio/bomc-deployment.yml

