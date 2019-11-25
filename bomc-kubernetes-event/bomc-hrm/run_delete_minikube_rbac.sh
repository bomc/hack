#!/bin/bash

echo #################################################
echo Delete hrm microservice minikube:rbac
echo -------------------------------------------------


echo Delete hrm rbac
kubectl delete -f ./deployment/istio/bomc-rabc.yml

