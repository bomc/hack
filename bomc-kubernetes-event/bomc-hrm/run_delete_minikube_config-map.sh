#!/bin/bash

echo #################################################
echo Delete hrm microservice minikube:configMap
echo -------------------------------------------------


echo Delete hrm configMap
kubectl delete -f ./deployment/istio/bomc-hrm-config-map.yml

