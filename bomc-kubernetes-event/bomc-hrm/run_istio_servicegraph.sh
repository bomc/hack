#!/bin/bash

echo #################################################
echo Start port-forwarding for servicegraph
echo -------------------------------------------------

echo Navigate to servicegraph:
echo .
http://localhost:8088/force/forcegraph.html
echo ..
http://localhost:8088/dotviz
echo ...
http://localhost:8088/dotgraph


# To run port-forwarding in background, use the following cli command, see end '&': 
# kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=servicegraph -o jsonpath="{.items[0].metadata.name}") 8088:8088 &

# NOTE: e.g. running port-forwarding for a pod in a special namespace, add the namespace at the end: -n bomc-app to the command.
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=servicegraph -o jsonpath="{.items[0].metadata.name}") 8088:8088
