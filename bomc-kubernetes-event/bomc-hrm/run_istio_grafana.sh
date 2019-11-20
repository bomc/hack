#!/bin/bash

echo #################################################
echo Start port-forwarding for grafana
echo -------------------------------------------------

echo Navigate to grafana dashboard with:
echo .
echo http://localhost:3000
echo .
echo Login as admin / admin

# To run port-forwarding in background, use the following cli command, see end '&': 
# kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000 &

# NOTE: e.g. running port-forwarding for a pod in a special namespace, add the namespace at the end: -n bomc-app to the command.
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000

