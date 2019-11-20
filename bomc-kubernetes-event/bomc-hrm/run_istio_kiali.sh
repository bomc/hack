#!/bin/bash

echo #################################################
echo Start port-forwarding for kiali
echo -------------------------------------------------

echo Navigate to kiali:
echo .
echo http://localhost:20001
echo .
echo Use default username and password: admin/admin

# To run port-forwarding in background, use the following cli command, see end '&': 
# kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=kiali -o jsonpath="{.items[0].metadata.name}") 20001:20001 &

# NOTE: e.g. running port-forwarding for a pod in a special namespace, add the namespace at the end: -n bomc-app to the command.
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=kiali -o jsonpath="{.items[0].metadata.name}") 20001:20001
