#!/bin/sh
PORT=${kubectl get svc -n istio-system kiali --output 'jsonpath={.spec.ports[*].nodePort}'}

echo=kiali runs on port:'$PORT'

kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=kiali -o jsonpath='{.items[0].metadata.name}') $PORT

Open in browser kiali-ui: 'https://192.168.99.100:$PORT' 