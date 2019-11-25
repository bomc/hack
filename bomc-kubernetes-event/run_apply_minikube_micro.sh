#!/bin/bash

echo #################################################
echo Apply all microservices minikube:deployments
echo -------------------------------------------------


echo Apply hrm microservices
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply order microservices
kubectl apply -f ./bomc-order/deployment/istio/bomc-deployment.yml

sleep 5s # waits 5 seconds

echo Apply invoice microservices
kubectl apply -f ./bomc-invoice/deployment/istio/bomc-deployment.yml

sleep 5s # waits 5 seconds
echo Apply configmap
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-hrm-config-map.yml

sleep 5s # waits 5 seconds
echo Apply rbac
kubectl apply -f ./bomc-hrm/deployment/istio/bomc-rbac.yml
