#!/bin/bash

echo #################################################
echo Apply hrm microservice minikube:configMap
echo -------------------------------------------------


echo Apply hrm configMap
kubectl apply -f ./deployment/istio/bomc-hrm-config-map.yml

