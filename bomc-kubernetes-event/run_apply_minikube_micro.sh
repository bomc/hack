#!/bin/bash

echo #################################################
echo Apply all microservices minikube:deployments
echo -------------------------------------------------


sleep 3s # waits 5 seconds
echo Apply secret
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-rbac.yml

sleep 3s # waits 5 seconds
echo Apply rbac
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-secret.yml

sleep 3s # waits 5 seconds
echo Apply configmap
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-config-map.yml

echo Apply hrm microservices
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply order microservices
kubectl apply -f ./bomc-order/deployment/istio/bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply invoice microservices
kubectl apply -f ./bomc-invoice/deployment/istio/bomc-deployment.ym

