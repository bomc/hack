#!/bin/bash

echo Read kafka-manager url, service is using NodePort:

minikube service -n bomc-app kafka-manager --url
