#!/bin/sh
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000

echo Open Grafana at http://localhost:3000/
