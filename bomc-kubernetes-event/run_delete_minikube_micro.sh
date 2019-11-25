#!/bin/bash

echo #################################################
echo Delete all microservices minikube:deployments
echo -------------------------------------------------


echo Delete hrm microservices
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-deployment.yml

sleep 1s # waits 1 seconds

echo Delete order microservices
kubectl delete -f ./bomc-order/deployment/istio/bomc-deployment.yml

sleep 1s # waits 1 seconds

echo Delete invoice microservices
kubectl delete -f ./bomc-invoice/deployment/istio/bomc-deployment.yml

sleep 1s # waits 1 seconds
echo Delete configmap
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-hrm-config-map.yml

sleep 1s # waits 1 seconds
echo Delete rbac
kubectl delete -f ./bomc-hrm/deployment/istio/bomc-rbac.yml
