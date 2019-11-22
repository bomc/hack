#!/bin/sh
PORT=${kubectl get svc -n istio-system kiali --output 'jsonpath={.spec.ports[*].nodePort}'}

# Open in browser kiali-ui: 'https://192.168.99.110:$PORT' 
echo=kiali runs on:'https://192.168.99.110:$PORT'

kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=kiali -o jsonpath='{.items[0].metadata.name}') $PORT
