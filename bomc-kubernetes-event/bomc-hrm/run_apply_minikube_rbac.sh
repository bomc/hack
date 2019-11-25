#!/bin/bash

echo #################################################
echo Apply hrm microservice minikube:rbac
echo -------------------------------------------------


echo Apply hrm rbac
kubectl apply -f ./deployment/istio/bomc-rbac.yml

