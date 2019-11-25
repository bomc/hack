#!/bin/bash

echo #################################################
echo Apply hrm microservice minikube:secret
echo -------------------------------------------------


echo Apply hrm secret
kubectl apply -f ./deployment/istio/bomc-hrm-secret.yml

