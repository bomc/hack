#!/bin/bash

echo #################################################
echo Apply hrm microservice minikube:deployments
echo -------------------------------------------------


echo Apply hrm microservices
kubectl apply -f ./deployment/istio/bomc-deployment.yml

echo Apply hrm microservices
kubectl apply -f ./deployment/istio/bomc-service.yml
