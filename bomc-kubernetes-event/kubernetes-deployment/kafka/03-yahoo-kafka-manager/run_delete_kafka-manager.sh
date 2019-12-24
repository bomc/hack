#!/bin/bash

echo Delete kafka-manager deployment.

kubectl delete -f ./kafka-manager-deployment.yml

sleep 5s # waits 5 seconds

echo Delete kafka-manager service.

kubectl delete -f ./kafka-manager-service.yml