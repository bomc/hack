#!/bin/bash

echo #################################################
echo Start port-forwarding for jaeger
echo -------------------------------------------------

echo Navigate to jaeger-query dashboard with:
echo .
echo http://localhost:16686

# To run port-forwarding in background, use the following cli command, see end '&': 
# kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686 &

# NOTE: e.g. running port-forwarding for a pod in a special namespace, add the namespace at the end: -n bomc-app to the command.
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686

