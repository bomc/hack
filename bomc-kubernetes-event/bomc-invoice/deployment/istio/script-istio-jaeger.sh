#!/bin/sh
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686

echo Open Jaeger tracing in browser at http://localhost:16686/
