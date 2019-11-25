#!/bin/bash

echo #################################################
echo Delete hrm microservice minikube:secret
echo -------------------------------------------------


echo Delete hrm secret
kubectl delete -f ./deployment/istio/bomc-secret.yml

