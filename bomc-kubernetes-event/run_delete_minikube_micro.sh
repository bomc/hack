#!/bin/bash

echo #################################################
echo Delete all microservices minikube:deployments
echo -------------------------------------------------



sleep 3s # waits 5 seconds
echo Delete secret
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-rbac.yml

sleep 3s # waits 5 seconds
echo Delete rbac
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-secret.yml

sleep 3s # waits 5 seconds
echo Delete configmap
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-config-map.yml

echo Delete hrm microservices
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-deployment.yml

sleep 1s # waits 1 seconds

echo Delete order microservices
kubectl delete -f ./bomc-order/deployment/istio/bomc-deployment.yml

sleep 1s # waits 1 seconds

echo Delete invoice microservices
kubectl delete -f ./bomc-invoice/deployment/istio/bomc-deployment.yml

