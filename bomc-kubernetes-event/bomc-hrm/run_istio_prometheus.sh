#!/bin/bash

echo #################################################
echo Start port-forwarding for prometheus
echo -------------------------------------------------

echo Navigate to prometheus:
echo .
echo http://localhost:9090/graph
echo or
echo http://localhost:9090/target

# To run port-forwarding in background, use the following cli command, see end '&': 
# kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 9090:9090 &

# NOTE: e.g. running port-forwarding for a pod in a special namespace, add the namespace at the end: -n bomc-app to the command.
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=prometheus -o jsonpath='{.items[0].metadata.name}') 9090:9090
